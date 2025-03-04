# TupleSpaces

Distributed Systems Project 2025

**Group T18**

*(choose one of the following levels and erase the other one)*  
**Difficulty level: I am Death incarnate! | Bring 'em on!**


### Team Members


| Number | Name              | User                              | Email                                  |
|--------|-------------------|-----------------------------------|----------------------------------------|
| 102637  | Gabriel Silva    | <https://github.com/lucaznch>     | <gabriel.da.silva@tecnico.ulisboa.pt>  |
| 106524  | Miguel Sousa     | <https://github.com/pombcoraft>   | <miguel.de.sousa@tecnico.ulisboa.pt>   | 
| 106581  | Afonso Dias      | <https://github.com/afonsodias37> | <afonso.lopes.dias@tecnico.ulisboa.pt> |

## Getting Started

The overall system is made up of several modules.
The definition of messages and services is in _Contract_.

See the [Project Statement](https://github.com/tecnico-distsys/Tuplespaces-2025) for a complete domain and system description.

### Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too -- just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```s
javac -version
mvn -version
```

### Installation

To compile and install all modules:

```s
mvn clean install
```

## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.
