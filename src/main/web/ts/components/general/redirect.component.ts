import {Component} from "@angular/core";
import {CanActivate, ComponentInstruction} from "@angular/router-deprecated";
import {SessionService} from "../../services/session.service";
import {ActivationRule} from "../../model/utils/activation-rule";

@Component({
    template: ''
})
@CanActivate((next: ComponentInstruction, previous: ComponentInstruction) => {
    return SessionService.canActivate([
        ActivationRule.activateOnRole("ROLE_PARTICIPANT", "/tasks"),
        ActivationRule.activateOnRole("ROLE_EDITOR", "/batch"),
        ActivationRule.activateOnRole("ROLE_NONE", "/security"),
    ], "/login")
})
export class RedirectComponent { }