package wraps;

import com.petpet.c3po.adaptor.fits.FITSAdaptor;
import com.petpet.c3po.adaptor.fits.FITSHelper;
import com.petpet.c3po.adaptor.rules.*;
import com.petpet.c3po.analysis.RepresentativeAlgorithmFactory;
import com.petpet.c3po.analysis.RepresentativeGenerator;
import com.petpet.c3po.api.adaptor.AbstractAdaptor;
import com.petpet.c3po.api.adaptor.ProcessingRule;
import com.petpet.c3po.api.model.Element;
import com.petpet.c3po.api.model.helper.Filter;
import com.petpet.c3po.api.model.helper.FilterCondition;
import com.petpet.c3po.dao.mongo.MongoPersistenceLayer;
import com.petpet.c3po.gatherer.LocalFileGatherer;
import com.petpet.c3po.utils.Configurator;
import com.petpet.c3po.utils.DataHelper;
import com.petpet.c3po.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;
import utils.PhotoConfigurator;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by artur on 27/10/15.
 */
public class C3POWrap {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    AbstractAdaptor adaptor;
    LinkedBlockingQueue<Element> q = new LinkedBlockingQueue<Element>(10000);
    List<String> samples=new ArrayList<String>();

    PhotoConfigurator cfg = PhotoConfigurator.getConfigurator();
    MongoPersistenceLayer pLayer;
    Map<String, String> config;
    Map<String, Class<? extends ProcessingRule>> knownRules;
    Map<String, Class<? extends AbstractAdaptor>> knownAdaptors;
    public C3POWrap(String host, String port, String dbname, String path_to_fits_results) {

        Configurator.getDefaultConfigurator().configure();

        pLayer = new MongoPersistenceLayer();
        Configurator.getDefaultConfigurator().setPersistence(pLayer);

        config = new HashMap<String, String>();
        config.put("db.host", host);
        config.put("db.port", port);
        config.put("db.name", dbname);
        config.put(com.petpet.c3po.common.Constants.OPT_COLLECTION_NAME, "test");
        config.put(com.petpet.c3po.common.Constants.OPT_COLLECTION_LOCATION, path_to_fits_results);
        config.put(com.petpet.c3po.common.Constants.OPT_INPUT_TYPE, "FITS");
        config.put(com.petpet.c3po.common.Constants.OPT_RECURSIVE, "True");

        DataHelper.init();
        XMLUtils.init();
        FITSHelper.init();
        knownAdaptors = new HashMap<String, Class<? extends AbstractAdaptor>>();
        knownAdaptors.put("FITS", FITSAdaptor.class);

        try {
            pLayer.establishConnection(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adaptor = new FITSAdaptor();
        knownRules = new HashMap<String, Class<? extends ProcessingRule>>();
        knownRules.put( com.petpet.c3po.common.Constants.CNF_ELEMENT_IDENTIFIER_RULE, CreateElementIdentifierRule.class );
        knownRules.put( com.petpet.c3po.common.Constants.CNF_EMPTY_VALUE_RULE, EmptyValueProcessingRule.class );
        knownRules.put( com.petpet.c3po.common.Constants.CNF_DROOLS_CONFLICT_RESOLUTION_RULE, DroolsConflictResolutionProcessingRule.class );
        knownRules.put(com.petpet.c3po.common.Constants.CNF_CONTENT_TYPE_IDENTIFICATION_RULE, ContentTypeIdentificationRule.class);
        knownRules.put(com.petpet.c3po.common.Constants.CNF_FILE_EXTENSION_IDENTIFICATION_RULE, FileExtensionIdentificationRule.class);



    }

    List<String> extract_samples() {

        cfg.setProperty(Constants.WEB_AJAX_STATUS, "Getting samples");

        List<String> props = new ArrayList<String>();
        props.add("digitalcamera_manufacturer");
        props.add("digitalcamera_modelname");
        props.add("file_extension");

        String alg = "distsampling";
        int size = 50;

        RepresentativeGenerator samplesGen = new RepresentativeAlgorithmFactory().getAlgorithm( alg );
        Map<String, Object> samplesOptions = new HashMap<String, Object>();
        samplesOptions.put( "properties", props );
        samplesGen.setOptions( samplesOptions );
        samplesGen.setFilter( new Filter( new FilterCondition( "collection", config.get(com.petpet.c3po.common.Constants.OPT_COLLECTION_NAME)) ) );

        List<String> result = samplesGen.execute(size);
        return result;
    }

    void uploadFITSmetadata() {

        Map<String, String> adaptorcnf = this.getAdaptorConfig(config, "FITS");
        LocalFileGatherer lfg = new LocalFileGatherer(config);
        adaptor.setConfig(adaptorcnf);
        List<ProcessingRule> rules = this.getRules("test");
        lfg.run();
        adaptor.setGatherer(lfg);
        adaptor.setQueue(q);
        adaptor.configure();
        adaptor.setRules(rules);
        adaptor.setCache(pLayer.getCache());

        cfg.setProperty(Constants.WEB_AJAX_STATUS, "Preparing data to calculate samples");
        adaptor.run();
        while (!q.isEmpty()) {
            pLayer.insert(q.poll());
        }
    }

    public void execute(){
        //uploadFITSmetadata();
        samples = extract_samples();
    }

    public List<String> getSamples(){
        return samples;
    }

    List<ProcessingRule> getRules(String name) {
        List<ProcessingRule> rules = new ArrayList<ProcessingRule>();
        rules.add(new AssignCollectionToElementRule(name)); // always on...
        for (String key : com.petpet.c3po.common.Constants.RULE_KEYS) {
            if (true) {
                Class<? extends ProcessingRule> clazz = knownRules.get(key);
                if (clazz != null) {
                    try {
                        logger.debug("Adding rule '{}'", key);
                        ProcessingRule rule = clazz.newInstance();
                        rules.add(rule);
                    } catch (InstantiationException e) {
                        logger.warn("Could not initialize the processing rule for key '{}'", key);
                    } catch (IllegalAccessException e) {
                        logger.warn("Could not access the processing rule for key '{}'", key);
                    }
                }
            }
        }
        return rules;
    }

    Map<String, String> getAdaptorConfig(Map<String, String> config, String prefix) {
        final Map<String, String> adaptorcnf = new HashMap<String, String>();
        for (String key : config.keySet()) {
            if (key.startsWith("c3po.adaptor.") || key.startsWith("c3po.adaptor." + prefix.toLowerCase())) {
                adaptorcnf.put(key, config.get(key));
            }
        }
        return adaptorcnf;
    }

}
