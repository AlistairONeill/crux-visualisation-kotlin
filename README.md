# crux-visualisation-kotlin
Experiment with ValidTime and how transactions work

Relies on AlistairONeill/crux-kotlin being installed in your local maven repository

The graph shows what colour you would retrieve from Crux based on a given transaction and valid time
The x-axis is Transaction Time and scales based on the actual transaction times.
The y-axis is the Valid Time and goes from 00:00:00 to 23:59:59 of the current day.

The time fields are expected to be formatted as 24 hours times.

Valid Time fields are optional (although, if End Valid Time is specified, Valid Time must also be specified)

Your transaction history appears in the left hand pane.