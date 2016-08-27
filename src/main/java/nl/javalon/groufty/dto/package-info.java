/**
 * Package containing Data Transfer Objects (DTOs). DTOs are used for transfering data between the client and
 * the server. Sometimes the format in which the server expects the data from the client doesn't exactly match
 * the format in the domain package, and then a DTO can be used. Examples include {@link nl.javalon.groufty.domain.task.Task}
 * which expects a {@link nl.javalon.groufty.domain.task.TaskList}, however a typical client would not submit
 * an entire tasklist but instead a tasklist id. The {@link nl.javalon.groufty.dto.task.TaskDto} exists to solve
 * this discrepancy.<br><br>
 * Layout of this package is identical to the domain package.
 */
package nl.javalon.groufty.dto;