import {Component} from "@angular/core";
import {RouteConfig, ROUTER_DIRECTIVES, CanActivate, ComponentInstruction, AsyncRoute} from "@angular/router-deprecated";
import {SessionService} from "../../../services/session.service";
import {ActivationRule} from "../../../model/utils/activation-rule";
import {GrouftyHTTPService} from "../../../services/http.service";

@Component({
    templateUrl: './templates/utils/router-container.template.html',
    directives: [ROUTER_DIRECTIVES]
})
@CanActivate((next: ComponentInstruction, previous: ComponentInstruction) => {
    return SessionService.canActivate([
        ActivationRule.activateOnRole("ROLE_PARTICIPANT"),
        ActivationRule.activateOnRole("ROLE_ANY", "/security")
    ], "/login");
})
@RouteConfig([
    new AsyncRoute({
        path: '/',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/student/tasks/student-tasks-overview.component')
            .then(m => m.StudentTasksOverviewComponent),
        name: 'StudentTasksOverview',
        useAsDefault: true
    }),
    new AsyncRoute({
        path: '/:id/',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/student/tasks/student-task-detail.component')
            .then(m => m.StudentTaskDetailComponent),
        name: 'StudentTaskDetail'
    })
])
export class StudentTasksRouterComponent { }