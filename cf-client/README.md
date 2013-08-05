Client
===================

The cf-java client brings in a boat load of dependencies and doesn't provide all the functionality that we need for
updating services so we wrote this lightweight client that fills those needs.

This client provides basic support for interacting with UAA to get OAuth tokens as well as support for getting tokens
from the .cf/tokens.yml file used by the 'cf' client.
