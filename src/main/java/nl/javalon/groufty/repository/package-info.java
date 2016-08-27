/**
 * Package containing the data repositories. These repositories interact with the database backing the application and as such are able to
 * retrieve model objects from it. Note that the repositories are defined by interfaces. The PagingAndSorting interface extends the CrudRespository interface
 * providing all needed methods for basic CRUD interaction with the database. The interfaces contained in this package are not to be implemented manually.
 * On run-time Spring will automatically creates specific implementations.
 */
package nl.javalon.groufty.repository;