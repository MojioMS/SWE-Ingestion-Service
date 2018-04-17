#### status: in development progress

[![Build Status](https://travis-ci.org/52North/SWE-Ingestion-Service.svg?branch=dev)](https://travis-ci.org/52North/SWE-Ingestion-Service)

# SWE-Ingestion-Service

The SensorWebEnabled-Ingestion-Service is an easy to configure and easy to deploy Data Flow Web application. It utilizes the Spring Cloud DataFlow Server technology and makes use of the Source-Processor-Sink pipelines stream approach.

## Libraries and Licenses

#### Third party libraries and licenses

|Library|License|Link/Source|
|:----:|:----:|:----:|
|Spring-framework|Apache License Version 2.0|[https://github.com/spring-projects/spring-framework/blob/master/src/docs/dist/license.txt](https://github.com/spring-projects/spring-framework/blob/master/src/docs/dist/license.txt)|

#### SWE-Ingestion-Service

|Library|License|Link/Source|
|:----:|:----:|:-----:|
|SWE-Ingestion-Service|Apache License Version 2.0|[https://github.com/MojioMS/SWE-Ingestion-Service/blob/master/LICENSE](https://github.com/MojioMS/SWE-Ingestion-Service/blob/master/LICENSE)|

## Requirements

1. A running instance of rabbitmq. A docker container is available:

`docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management`

2. A running instance of the Local Spring Cloud DataFlow Server. We recommend Version 1.4.0.RC1 available at [https://cloud.spring.io/spring-cloud-dataflow/](https://cloud.spring.io/spring-cloud-dataflow/) After download, run the local server with:

`java -jar spring-cloud-dataflow-server-local-1.4.0.RC1.jar`

## Installation

  1. Clone the repository: `git clone https://github.com/52north/SWE-Ingestion-Service`.

  2. Build the project: `mvn clean install`.

  3. Set the correct filepath to your repository [in line 6 of rest-controller/src/main/resources/application.yml](https://github.com/52North/SWE-Ingestion-Service/blob/dev/rest-controller/src/main/resources/application.yml#L6),

## How to Run

  1. Start `RestController`.

## API:

#### generell:

	* GET [http://localhost:8080/api](http://localhost:8080/api) - gets a list of resources.

#### Sources:

  * GET [http://localhost:8080/api/sources](http://localhost:8080/api/sources) - gets all registered sources.

#### Processors:

  * GET [http://localhost:8080/api/processors](http://localhost:8080/api/processors) - gets all registered processors.

#### Sinks:

  * GET [http://localhost:8080/api/sinks](http://localhost:8080/api/sinks) - gets all registered sinks.

#### Streams:

  * GET [http://localhost:8080/api/streams](http://localhost:8080/api/streams) - gets all registered streams.

  * GET api/streams/{streamName} produces=application/json - gets the registered stream with name `streamName` or 404 if that stream is not found.

  * GET api/streams/{streamName} produces=application/xml - gets the sensorml processDescription of the registered stream with name `streamName` or 404 if the stream or its sensorML processDescription is not found.

  * POST api/streams consumes=application/xml - registers a new Stream.
    RequestBody payload example:
	```
	<?xml version="1.0" encoding="UTF-8"?>
<sml:AggregateProcess gml:id="sdc" xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:sml="http://www.opengis.net/sensorml/2.0" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd" xsi:schemaLocation="http://www.opengis.net/sensorml/2.0 http://schemas.opengis.net/sensorML/2.0/sensorML.xsd">
   <sml:components>
      <sml:ComponentList>
         <sml:component name="source_output">
            <sml:PhysicalSystem gml:id="marine-fluo">
               <gml:identifier codeSpace="uniqueID">WL-ECO-FLNTU-4476</gml:identifier>
               <sml:identification>
                  <sml:IdentifierList>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0002/">
                           <sml:label>Long name</sml:label>
                           <sml:value>Galway Bay Cable Observatory Fluorometer</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0006/">
                           <sml:label>Short name</sml:label>
                           <sml:value>Galway Bay Cable Observatory Fluorometer</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0012/">
                           <sml:label>Manufacturer</sml:label>
                           <sml:value>WetLabs</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0003/">
                           <sml:label>Model name</sml:label>
                           <sml:value>ECO-FLNTU</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0005/">
                           <sml:label>Serial number</sml:label>
                           <sml:value>3137</sml:value>
                        </sml:Term>
                     </sml:identifier>
                  </sml:IdentifierList>
               </sml:identification>
               <sml:featuresOfInterest>
                  <sml:FeatureList definition="http://www.opengis.net/def/featureOfInterest/identifier">
                     <swe:label>Galway Bay Cable Observatory</swe:label>
                     <sml:feature xlink:href="Galway Bay Cable Observatory"/>
                  </sml:FeatureList>
               </sml:featuresOfInterest>
               <sml:outputs>
                  <sml:OutputList>
                     <sml:output name="fluorometerOutput">
                        <sml:DataInterface>
                           <sml:data>
                              <swe:DataStream>
                                 <swe:elementType name="fluorometerOutputStream">
                                    <swe:DataRecord>
                                       <swe:field name="datetime">
                                          <swe:Time definition="http://www.opengis.net/def/property/OGC/0/ResultTime">
                                             <swe:label>Date and time</swe:label>
                                             <swe:description>Date-time is a time stamp from a Global Positioning System receiver at the cable observatory shore station in the format YYYY-MM-DDThh:mm:ss.sss .</swe:description>
                                             <swe:uom/>
                                          </swe:Time>
                                       </swe:field>
                                       <swe:field name="instrument_id">
                                          <swe:Text>
                                             <swe:label>Instrument ID</swe:label>
                                             <swe:description>Instrument-ID is a unique identifier for the instrument based on its manufacturer, model number and serial number</swe:description>
                                          </swe:Text>
                                       </swe:field>
                                       <swe:field name="Instument_datetime">
                                          <swe:Text definition="http://www.opengis.net/def/property/OGC/0/PhenomenonTime">
                                             <swe:label>Instrument date</swe:label>
                                             <swe:description>Instrument clock date in the format MM/DD/YYYY</swe:description>
                                          </swe:Text>
                                       </swe:field>
                                       <swe:field name="Wavelength_for_Fluorescence_Measurements">
                                          <swe:Quantity>
                                             <swe:label>Wavelength of light used to make fluorescence measurements</swe:label>
                                             <swe:description>Wavelength of light used to make fluorescence measurements in nanometres</swe:description>
                                             <swe:uom code="nm"/>
                                          </swe:Quantity>
                                       </swe:field>
                                       <swe:field name="Fluorescence">
                                          <swe:Count definition="http://vocab.nerc.ac.uk/collection/B39/current/fluorescence/">
                                             <swe:label>Fluorescence</swe:label>
                                             <swe:description>Chlorophyll fluorometer instrument output (counts) (no units)</swe:description>
                                          </swe:Count>
                                       </swe:field>
                                       <swe:field name="Wavelength_for_Turbidity_Measurements">
                                          <swe:Quantity>
                                             <swe:label>Wavelength of light used to make turbidity measurements</swe:label>
                                             <swe:description>Wavelength of light used to make turbidity measurements in nanometres</swe:description>
                                             <swe:uom code="nm"/>
                                          </swe:Quantity>
                                       </swe:field>
                                       <swe:field name="Turbidity">
                                          <swe:Count definition="http://vocab.nerc.ac.uk/collection/P01/current/TURBXXXX/">
                                             <swe:label>Turbidity</swe:label>
                                             <swe:description>Optical scattering turbidity sensor instrument output (counts) (no units)</swe:description>
                                          </swe:Count>
                                       </swe:field>
                                       <swe:field name="Thermistor">
                                          <swe:Quantity>
                                             <swe:uom/>
                                          </swe:Quantity>
                                       </swe:field>
                                    </swe:DataRecord>
                                 </swe:elementType>
                                 <swe:encoding>
								 <!-- Abhängig vom EncodingTypen den Processor wählen.-->
								 <!-- Es gibt \{TextEncoding} auch: {XmlEncoding, BinaryEncoding}-->
                                    <swe:TextEncoding tokenSeparator=";" blockSeparator="\n"/>
                                 </swe:encoding>
                                 <swe:values/>
                              </swe:DataStream>
                           </sml:data>
                           <sml:interfaceParameters>
                              <swe:DataRecord definition="https://52north.org/swe-ingestion/mqtt/3.1">
                                 <swe:field name="mqtt_broker_url">
                                    <swe:Text definition="https://52north.org/swe-ingestion/mqtt/3.1#url">
                                       <swe:value>tcp://nexos.dev.52north.org:1883</swe:value>
                                    </swe:Text>
                                 </swe:field>
                                 <swe:field name="mqtt_broker_topics">
                                    <swe:Text definition="https://52north.org/swe-ingestion/mqtt/3.1#topics">
                                       <swe:value>spiddal-fluorometer</swe:value>
                                    </swe:Text>
                                 </swe:field>
                              </swe:DataRecord>
                           </sml:interfaceParameters>
                        </sml:DataInterface>
                     </sml:output>
                  </sml:OutputList>
               </sml:outputs>
               <sml:position>
                  <swe:Vector referenceFrame="urn:ogc:def:crs:EPSG::4326">
                     <swe:coordinate name="easting">
                        <swe:Quantity axisID="x">
                           <swe:uom code="degree"/>
                           <swe:value>-9.265783</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                     <swe:coordinate name="northing">
                        <swe:Quantity axisID="y">
                           <swe:uom code="degree"/>
                           <swe:value>53.227317</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                     <swe:coordinate name="altitude">
                        <swe:Quantity axisID="z">
                           <swe:uom code="m"/>
                           <swe:value>52.0</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                  </swe:Vector>
               </sml:position>
            </sml:PhysicalSystem>
         </sml:component>
         <sml:component name="sos_input">
            <sml:PhysicalSystem gml:id="marine-fluo-sos">
               <gml:identifier codeSpace="uniqueID">WL-ECO-FLNTU-4476</gml:identifier>
               <sml:identification>
                  <sml:IdentifierList>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0002/">
                           <sml:label>Long name</sml:label>
                           <sml:value>Galway Bay Cable Observatory Fluorometer</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0006/">
                           <sml:label>Short name</sml:label>
                           <sml:value>Galway Bay Cable Observatory Fluorometer</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0012/">
                           <sml:label>Manufacturer</sml:label>
                           <sml:value>WetLabs</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0003/">
                           <sml:label>Model name</sml:label>
                           <sml:value>ECO-FLNTU</sml:value>
                        </sml:Term>
                     </sml:identifier>
                     <sml:identifier>
                        <sml:Term definition="http://vocab.nerc.ac.uk/collection/W07/current/IDEN0005/">
                           <sml:label>Serial number</sml:label>
                           <sml:value>3137</sml:value>
                        </sml:Term>
                     </sml:identifier>
                  </sml:IdentifierList>
               </sml:identification>
               <sml:featuresOfInterest>
                  <sml:FeatureList definition="http://www.opengis.net/def/featureOfInterest/identifier">
                     <swe:label>Galway Bay Cable Observatory</swe:label>
                     <sml:feature xlink:href="Galway Bay Cable Observatory"/>
                  </sml:FeatureList>
               </sml:featuresOfInterest>
               <sml:outputs>
                  <sml:OutputList>
                     <sml:output name="Fluorescence">
                        <swe:Count definition="http://vocab.nerc.ac.uk/collection/B39/current/fluorescence/">
                           <swe:label>Fluorescence</swe:label>
                           <swe:description>Chlorophyll fluorometer instrument output (counts) (no units)</swe:description>
                        </swe:Count>
                     </sml:output>
                     <sml:output name="Turbidity">
                        <swe:Count definition="http://vocab.nerc.ac.uk/collection/P01/current/TURBXXXX/">
                           <swe:label>Turbidity</swe:label>
                           <swe:description>Optical scattering turbidity sensor instrument output (counts) (no units)</swe:description>
                        </swe:Count>
                     </sml:output>
                  </sml:OutputList>
               </sml:outputs>
               <sml:position>
                  <swe:Vector referenceFrame="urn:ogc:def:crs:EPSG::4326">
                     <swe:coordinate name="easting">
                        <swe:Quantity axisID="x">
                           <swe:uom code="degree"/>
                           <swe:value>-9.265783</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                     <swe:coordinate name="northing">
                        <swe:Quantity axisID="y">
                           <swe:uom code="degree"/>
                           <swe:value>53.227317</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                     <swe:coordinate name="altitude">
                        <swe:Quantity axisID="z">
                           <swe:uom code="m"/>
                           <swe:value>52.0</swe:value>
                        </swe:Quantity>
                     </swe:coordinate>
                  </swe:Vector>
               </sml:position>
            </sml:PhysicalSystem>
         </sml:component>
      </sml:ComponentList>
   </sml:components>
   <sml:connections>
      <sml:ConnectionList>
         <sml:connection>
            <sml:Link>
               <sml:source ref="components/marine-fluo/outputs/Fluorescence"/>
               <sml:destination ref="components/marine-fluo-sos/outputs/Fluorescence"/>
            </sml:Link>
         </sml:connection>
         <sml:connection>
            <sml:Link>
               <sml:source ref="components/marine-fluo/outputs/Turbidity"/>
               <sml:destination ref="components/marine-fluo-sos/outputs/Turbidity"/>
            </sml:Link>
         </sml:connection>
      </sml:ConnectionList>
   </sml:connections>
</sml:AggregateProcess>
	```
    Response:
    * 201 - Created with json response of the created Stream, e.g. 
	```
	{
   	 "name": "sb68a63d8-cc63-4ce3-9212-09b7a1f47740",
    	"status": "undeployed",
   	 "definition": "mqtt-source-rabbit --url=tcp://nexos.dev.52north.org:1883 --topics=spiddal-fluorometer | csv-processor | db-sink"
}
	```
	* 400 - "swe:Text definition {optionUrl} requires a hashtag ( # ) option."
	* 400 - "Option {appOptionName} is not supported by source {sourceName}."
	* 404 - "No supported Source found for DataRecord definition {dataRecordDefinition}"
	* 404 - "DataRecord definition {dataRecordDefinition} is supposed to be supported by Source {sourceName}, but Source {sourceName} not found."
    * 409 - "A stream with that name is already created."
    * 400 - more errors of various meanings possible.

  * POST api/streams Content-Type: application/xml - (see previous)
  
  * PUT api/streams/{streamName} , consumes=application/json - changes the status of the registered Stream 'streamName' according to the requests payload.
  Example Payload:
  ```
  {
    	"status": "deployed"
}
  ```
    * 200 - The Streams status has been changed to the requested status
	* 202 - "The Stream {streamName} is currently `deploying` and thus, the resource status will not be changed."
	* 400 - "Request is missing required field `status`."
	* 400 - "The requested status `statusValue` is not supported. Supported status are: 'deployed' and 'undeployed'."
    * 404 - "Stream {streamName} not found.

## Configuration

#### TO DO:


## Bugs and Feedback **TODO: fix this**
Developer feedback goes a long way towards making this adapter even better. Submit a bug report or request feature enhancements to [via mail to s.jirka@52north.org](mailto:s.jirka@52north.org?Subject=SWE-Ingestion-Service) or open a issue on this github repository.

## Funding
**TODO: this**
