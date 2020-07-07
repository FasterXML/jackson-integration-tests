# Overview

This project contains integration tests that try to ensure compatibility across all official
Jackson components (ones owned by FasterXML org in Github), for latest patch version of
all open Jackson minor branches since version 2.12.

Project is designed so it may be run as needed as well as automatically by Travis.
Although it would be nice to have it run triggered by changes to components being tested,
the current plan is to instead run it on daily basis due to complications in trying to
track dependencies.

## Tests included

### Core vs Module compatibility

The first category of tests covers simplest smoke tests to ensure that format and datatype
modules pass against matching "core" components -- essentially a subset of tests that individual
module repos have. While this may seem odd (why duplication?), the benefit is to allow running
of this subset of tests not only when module itself changes, but also when core components change
(even if with some delay, if run once a day).

Currently included modules are:

[to be added]

### Cross-module compatibility

Beyond compatibility between core and extension modules, another group of basic tests is
that of "cross-module" compatibility, for cases where neither module has dependency on the
other. For example, a specific format module may have different requirements for handling:

* XML format does not have native numeric types, so do numeric Date/Time values ("timestamps") work?
* Some formats support concept of `FormatSchema`; some have native Binary value types: does this affect handling of specific datatypes regarding Schema generation of 3rd party datatypes?

Current included tests include:

[to be added]



