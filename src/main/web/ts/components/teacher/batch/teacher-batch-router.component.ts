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
        ActivationRule.activateOnRole("ROLE_EDITOR"),
        ActivationRule.activateOnRole("ROLE_ANY", "/security")
    ], "/login");
})
@RouteConfig([
    new AsyncRoute({
        path: '/',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/batch/teacher-batch-import.component')
            .then(m => m.TeacherBatchImportComponent),
        name: 'TeacherBatchImport',
        useAsDefault: true
    }),
])
export class TeacherBatchRouterComponent { }