# crux-visualisation-kotlin
Experiment with ValidTime and how transactions work

# Installation

This project relies on [AlistairONeill/crux-kotlin](https://github.com/AlistairONeill/crux-kotlin) being installed in your local maven repository:

After cloning crux-kotlin, navigate to its directory and then run

`mvn install`

This will install the required dev-SNAPSHOT to your local Maven repo.

Now navigate to where you cloned crux-visualisation

`mvn package`

This will run the test suite and create a jar

`java -jar ./target/crux-visualisation-1.0-SNAPSHOT-jar-with-dependencies.jar`

Will run the compiled jar

The graph shows what colour you would retrieve from Crux based on a given transaction and valid time
The x-axis is Transaction Time and scales based on the actual transaction times.
The y-axis is the Valid Time and goes from 00:00:00 to 23:59:59 of the current day.

The time fields are expected to be formatted as 24 hours times.

Valid Time fields are optional (although, if End Valid Time is specified, Valid Time must also be specified)

Your transaction history appears in the left-hand pane.