import {Component, OnInit} from "@angular/core";
import {ROUTER_DIRECTIVES, CanActivate, ComponentInstruction} from "@angular/router-deprecated";
import {SessionService} from "../../services/session.service";
import {ActivationRule} from "../../model/utils/activation-rule";
import {Role} from "../../model/users/user";

@Component({
    templateUrl: './templates/general/security.component.html',
    directives: [ROUTER_DIRECTIVES]
})
@CanActivate((next: ComponentInstruction, previous: ComponentInstruction) => {
    return SessionService.canActivate([ActivationRule.activateOnSession()], "/login");
})
export class SecurityComponent implements OnInit {

    public role : Role = "ROLE_NONE";

    public constructor(private _sessionService: SessionService) {}

    public ngOnInit() {
        this._sessionService.resolveUser().then(user => {
            if (user == null) {
                this.role = "ROLE_NONE";
            } else {
                this.role = user.authority;
            }
        });
    }

}