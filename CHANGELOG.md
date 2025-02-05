# Changelog

## v5.2.5 (06/12/2023)
#### Bug fixes:

- Error sending ingestion failure notification via email #3023 

---

To try out this version, check the [install instructions](https://github.com/keeps/roda/blob/master/deploys/standalone/README.md).
---

## v5.2.4 (10/11/2023)
#### Enhancements:

- Update Swedish translation language

---

To try out this version, check the [install instructions](https://github.com/keeps/roda/blob/master/deploys/standalone/README.md).


---

## v5.2.3 (10/11/2023)
#### What's new:

- New German (Austrian) translation of the Web interface :austria: 

#### Bug fixes:

- Create folder access-keys when initializing RODA for the first time #2992
- Add default representation type when creating a preservation action job #2990
- Edit button for selecting parent does not work as expected #2988
- EAD 2002 dissemination crosswalk duplicates record group level #2987

#### Enhancements:

- Add title attribute to improve accessibility #2989

#### Security:

- Bump several dependencies

---

To try out this version, check the [install instructions](https://github.com/keeps/roda/blob/master/deploys/standalone/README.md).


---

## v5.2.2 (04/10/2023)
#### Bug fixes:
- Fixed FileID when it is encoded #2963
- Fixed API filter issue #2965

#### Security:
- Several dependency upgrades to fix security vulnerabilities

---

To try out this version, check the [install instructions](https://www.roda-community.org/deploys/standalone/).


---

## v5.2.1 (08/09/2023)
#### Bug fixes:
- Listing RODA objects via REST-API is not showing any results #2935
- Preservation events page is not showing no events #2928
- REST API endpoint to retrieve the last transferred resource report does not show the reports #2929
- Problem with pre-filter not being reset when searching preservation events #2941

#### Security:
- Several dependency upgrades to fix security vulnerabilities

---

To try out this version, check the [install instructions](https://www.roda-community.org/deploys/standalone/).


---

## v5.2.0 (28/07/2023)
#### Enhancements:
- DIP must be deleted if it no longer contains any link with any entity. #2863
- Ingest job report could expose if SIP is update #2212

#### Bug fixes:
- Unexpected behaviour can cause index to be completely deleted #2921

#### Security:
- Several dependency upgrades to fix security vulnerabilities
- Remove python from Docker image

---

To try out this version, check the [install instructions](https://www.roda-community.org/deploys/standalone/).


---

## v5.1.0 (20/06/2023)
#### New features:

- Added property to differentiate environments #2676 
- Added link to RODA Marketplace in Menu #2722
- Added links to additional features #2723
- Added marketplace text to welcome page #2724
- Option to enable AIP locking when editing descriptive metadata #2672
- Preview functionality in disposal rules with AIPs affected by #2664 

#### Enhancements:
- Reduce indexed information of the entities that spend much of the index #2058
- Partial updates are not affecting the updatedOn field #2851
- Updated the banner #2725

#### Bug fixes:
- Minimal ingest plugin is using E-ARK SIP 1.0 as SIP format instead of E-ARK SIP 2.0.4 #2736 
- Could not resolve type id 'AndFiltersParameters' #2809
- Access token can only be created if RODA is instantiated as CENTRAL #2881
- Saved search for files associated to a representation information not working properly #2671 

#### Security:
- Remove xml-beans dependency #2726 

---

To try out this version, check the [install instructions](https://www.roda-community.org/deploys/standalone/).


---

## v4.5.6 (04/05/2023)
#### Bug fixes:

- Option to disable user registration on server-side #2840 

Install for demonstration:
```
docker pull ghcr.io/keeps/roda:v4.5.6
```
---

## v5.1.0-RC (17/04/2023)

---

## v4.5.5 (16/03/2023)
#### Dependencies upgrade:
- Bump commons-ip version from 2.3.0 to 2.3.2 


Install for demonstration:
```
docker pull ghcr.io/keeps/roda:v4.5.5
```
---

## v5.0.0 (13/03/2023)
### :warning: Breaking Changes
RODA  5.X will use Apache Solr 9 as indexing system. If you have an existing RODA implementation with Solr 8 you will need to [upgrade the Solr to version 9](https://solr.apache.org/guide/solr/latest/upgrade-notes/major-changes-in-solr-9.html) and then rebuild all indexes on RODA.

RODA 5.X docker now runs as the user roda (uid: 1000, gid: 1000) to improve security. This may affect you current implementation as it may lack enough permissions to access the storage. To fix, change the owner or permissions of the files and directories in the mapped volumes or binded folders. Alternatively, you can [change the RODA user uid](https://docs.docker.com/compose/compose-file/#user) in docker compose. 

---

#### New features:

- Distributed Digital Preservation #1933 #1934 #1935
- Added authentication via Access Token (for REST-API)
- Support binaries as a reference (shallow SIP/AIP) #786
- Adds list of all available plugins (see [RODA Marketplace](https://market.roda-community.org/)) #2323
- Supports verified plugins #2323
- New Swedish translation of the Web interface :sweden:
- Updates to Hungarian translation of the Web interface :hungary:

#### Changes:

- Upgraded from Java 8 to Java 17
- Upgraded from Apache Solr 8 to Apache Solr 9
- Upgraded from Apache Tomcat 8.5 to Apache Tomcat 9

#### Security:

- RODA docker now runs as roda (uid: 1000) instead of root
- (Applicational) Users can now have JWT access tokens to access the REST-API
- Option to restrict user web authentication to delegated (CAS) or JWT access tokens
- Several dependency upgrades to fix security vulnerabilities
- CVE-2016-1000027 (spring-web 5.3.24): RODA does not use the HTTPInvokerServiceExporter or RemoteInvocationSerializingExporter classes, therefore we are [NOT affected](https://github.com/spring-projects/spring-framework/issues/24434#issuecomment-744519525) by this vulnerability 
- CVE-2022-1471 (snake-yaml 1.33): RODA does not use [empty constructor](https://snyk.io/blog/unsafe-deserialization-snakeyaml-java-cve-2022-1471/) so we are NOT affected by this vulnerability.

---

We would like to thank the contributions of:
- [WhiteRed](https://www.whitered.se/) with the Swedish translation :sweden:
- Panka Dióssy from the [National Laboratory for Digital Heritage](https://dh-lab.hu/), with updates to the Hungarian translation :hungary:

---

To try out this version, check the [install instructions](https://github.com/keeps/roda/blob/master/deploys/standalone/README.md).


---

## v4.5.4 (27/01/2023)
#### Enhancements:

- Add metric per percentage of retries #2299

Install for demonstration:
```
docker pull keeps/roda:v4.5.4
```
---

## v4.5.3 (25/01/2023)
#### Bug fixes:

- Support very large queries to Solr (fix regression) #2311

#### Enhancements:

- Add icon to experimental plugin categories #2306

Install for demonstration:
```
docker pull keeps/roda:v4.5.3
```
---

## v4.5.2 (19/01/2023)
#### Bug fixes:

- Failsafe fallback policy misconfigured #2303

Install for demonstration:
```
docker pull keeps/roda:v4.5.2
```
---

## v4.5.1 (16/01/2023)
#### Enhancements:

- Refactor RetryPolicyBuilder #2296
- Improve log information during initialization process #2297
- Add metrics about retries (related to RetryPolicyBuilder) #2298

Install for demonstration:
```
docker pull keeps/roda:v4.5.1
```

---

## v4.5.0 (06/01/2023)
### :warning: Breaking Changes
Due to a dependency upgrade from Apache DS a migration procedure should be executed:

1. Perform a LDAP backup
2. Remove the ldap folder inside config directory
3. Start RODA
4. Restore the LDAP backup.

#### New features:

- Solr retry #1216
- Add saved search functionality #2283

#### Enhancements:

- Dialogs.prompt() lack of feedback when input is invalid #908

#### Bug Fixes:

- Fixity information computation does not create an event when skipped #2291
- File leak when listing disposal resources #2250
- Ingest Assessment search filter does not clear up #2263

#### Security:

- Several dependency upgrades to fix security vulnerabilities

Install for demonstration:
```
docker pull keeps/roda:v4.5.0
```

---

## v3.7.1 (01/08/2022)
#### Bug Fixes:

- Processes with different types of parallelism are sharing the same pool of workers #2211
- Deleting a Representation is also deleting PREMIS file #2033

Install for demonstration:
```
docker pull keeps/roda:v3.7.1
```
---

## v4.4.0 (17/06/2022)
### :warning: Breaking Changes
Solr 7.7 reached EOL meaning that is no longer supported and will not receive any security patches. As such RODA from version 4.4 onward will use Solr 8 as index system. If you have any implementation with Solr 7 you need to upgrade the Solr to version 8 and then rebuild all indexes on RODA.

#### New features:

- Upgrade Solr version from 7.7 to Solr 8.11.1
- Upgrade GWT version from 2.9.0 to 2.10.0

Install for demonstration:
```
docker pull keeps/roda:v4.4.0
```
---

## v4.3.1 (17/06/2022)
#### Bug Fixes:

- Edit descriptive metadata from an AIP with a disposal schedule gives an error #2190 
- Multiple plugin assumes last plugin state in final report #2067
- Preservation event and incident risk counters on representation panel #2064

Install for demonstration:
```
docker pull keeps/roda:v4.3.1
```


---

## v4.3.0 (26/04/2022)
#### New features:

-  Akka events with Zookeeper seed registration [#2001](https://github.com/keeps/roda/issues/2001)

#### Enhancements:

-  Add error message to ClientLogger for fatal method [#2002](https://github.com/keeps/roda/issues/2002)

#### Bug Fixes:

-  Ambiguous representation PREMIS relatedObjectIdentifierValue [#1993](https://github.com/keeps/roda/issues/1993)
-  Classification scheme won't load because of unrecognized field "type" [#1986](https://github.com/keeps/roda/issues/1986)
-  Reject assessment is creating premis events on AIP, change this event for Repository level [#1984](https://github.com/keeps/roda/issues/1984)

Install for demonstration:
```
docker pull keeps/roda:v4.3.0
```

---

## v3.7.0 (21/04/2022)

#### New features:

-  Akka events with Zookeeper seed registration [#2001](https://github.com/keeps/roda/issues/2001)

#### Enhancements:

-  Add error message to ClientLogger for fatal method [#2002](https://github.com/keeps/roda/issues/2002)

Install for demonstration:
```
docker pull keeps/roda:v3.7.0
```

---

## v3.6.4 (11/03/2022)
#### Bug Fixes:

-  Fix job orchestration displayed badges

Install for demonstration:
```
docker pull keeps/roda:v3.6.4
```

---

## v3.6.3 (11/03/2022)

#### Enhancements:

-  Remove trace logs from logback configuration [#1994](https://github.com/keeps/roda/issues/1994)

#### Bug Fixes:

-  Generalized noneselect option when building search filter [#1995](https://github.com/keeps/roda/issues/1995)

Install for demonstration:
```
docker pull keeps/roda:v3.6.3
```

---

## v3.6.2 (17/02/2022)
### Bug fixes:

- CAS Login issue when user with the same email already exists #1988

Install for demonstration:
```
docker pull keeps/roda:v3.6.2
```
---

## v4.2.1 (15/02/2022)
### Bug fixes:

- CAS Login issue when user with the same email already exists #1988

### Enhancements:

- Add under appraisel status to transferred resources deletion #1989
- Replace embed marked.js by webjar #1983

Install for demonstration:
```
docker pull keeps/roda:v4.2.1
```

---

## v3.6.1 (26/01/2022)
### Bug fixes:

- Update Dockerfile base image

Install for demonstration:
```
docker pull keeps/roda:v3.6.1
```

---

## v4.2.0 (17/01/2022)
### New features:

- Job orchestration #1981
- Add prometheus metrics for HTTP notification system #1982

### Bug fixes:

- LinkingObjectIdentifierValue links to unknown URN (in preservation metadata PREMIS) #1946
- Object PREMIS does not register contentLocation in storage element #1947
- Fixity information computation to report SKIPPED #1970
- Allow run mutiple plugins #1977

Install for demonstration:
```
docker pull keeps/roda:v4.2.0
```

---

## v3.6.0 (17/01/2022)
New features:

- Job orchestration #1981
- Add prometheus metrics for HTTP notification system #1982

Install for demonstration:
```
docker pull keeps/roda:v3.6
```
---

## v4.1.1 (09/12/2021)

#### Enhancements:

-  Add an option to always display the last descriptive metadata [#1965](https://github.com/keeps/roda/issues/1965)

#### Bug Fixes:

-  Ingest assessment not working with filter [#1964](https://github.com/keeps/roda/issues/1964)

Install for demonstration:
```
docker pull keeps/roda:v4.1.1
```
---

## v3.5.7 (17/01/2022)
#### Enhancements:
- Expand Portal to allow customization via AIP level #1969


Install for demonstration:
```
docker pull keeps/roda:v3.5.7
```