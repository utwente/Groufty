import {FormBuilder, ControlGroup, Control, Validators} from "@angular/common";
import {Component} from "@angular/core";
import {Router} from "@angular/router-deprecated";
import {AlertComponent} from "../utils/alert.component";
import {MessagesComponent} from "../utils/messages.component";
import {Message} from "../../model/utils/message";
import {SessionService} from "../../services/session.service";
import {SessionCredentials} from "../../model/utils/session-credentials";

@Component({
    templateUrl: './templates/general/login.component.html',
    directives: [AlertComponent, MessagesComponent]
})
export class LoginComponent {

    private _creds : SessionCredentials = new SessionCredentials();
    private _loginForm: ControlGroup;
    public userId : Control;
    public password : Control;
    public busy : boolean = false;
    public messages : Message[] = [];

    public constructor(private _router: Router,
                       private _builder: FormBuilder,
                       private _sessionService : SessionService) {
        this.userId = new Control('', Validators.required);
        this.password = new Control('', Validators.required);
        this._loginForm = this._builder.group({
            userId: this.userId,
            password: this.password
        });
    }

    public login() : void {
        this.messages.splice(0, this.messages.length);
        this.busy = true;
        if (this._creds.password == "") {
            delete this._creds.password;
        }
        this._sessionService.login(this._creds,
            () => {
                this._router.navigate(['Start']);
            }, error => {
                if (error.status == 403) {
                    this.messages.push(new Message("Username or password incorrect.", "danger"));
                } else {
                    this.messages.push(new Message(<any>error.text(), "danger"));
                }
                this.busy = false;
            }, () => {
                this.busy = false;
            }
        );
    }
}