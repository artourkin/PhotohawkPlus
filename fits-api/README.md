# fits-api
fits wrapped.

FITS (File Information Tool Set) is developed at Harvard. 
See [http://projects.iq.harvard.edu/fits](http://projects.iq.harvard.edu/fits)

The tool set is somehow a bit awkward to use from a Maven project. This
small project wraps an instance of Fits and handles dependency management.


## Managing Dependencies
FITS comes with a lot of dependencies on third party jars.
All of these dependencies are in the FITS download which is
incorporated in this code base under `fits-{version}`.
This project integrates these dependencies with the maven
life cycle by using the plugin
_com.googlecode.addjars-maven-plugin_. This plugin will add all of
the jars found recursively at a given location to your local
.m2 repository.

We tossed out dependencies on some libraries and replaced these
with libraries of a newer version. Tossed out libraries are in the
`libs-removed` directory.


## Building the project
After cloning this project to local disk, follow these steps:

- Comment out the dependency section in the `pom.xml` between

			START dependencies added to .m2 by addjars-maven-plugin
			...
			END dependencies added to .m2 by addjars-maven-plugin

- Built the project from the command line: 

			$ mvn clean install

- Before opening the project under an editor like Eclipse, undo the commenting out from step 1.

## Using the project
After building the project locally (see above) you can declare a dependency on this project
in your pom.xml. 

		<dependency>
			<groupId>nl.knaw.dans</groupId>
			<artifactId>fits-api</artifactId>
			<version>1.0</version>
		</dependency>
		
---

