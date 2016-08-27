/// <reference path="../typings/index.d.ts" />

import {LocationStrategy, HashLocationStrategy} from "@angular/common";
import {provide, ComponentRef} from "@angular/core";
import {HTTP_PROVIDERS} from "@angular/http";
import {bootstrap} from "@angular/platform-browser-dynamic";
import {ROUTER_PROVIDERS} from "@angular/router-deprecated";

import {AppComponent} from "./components/general/app.component";
import {GrouftyHTTPService} from "./services/http.service";
import {SessionService} from "./services/session.service";
import {grouftyInjector} from "./utils/groufty.injector";

bootstrap(AppComponent, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    GrouftyHTTPService,
    SessionService,
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]).then((appRef: ComponentRef<AppComponent>) => {
    // Workaround: Store current injector service for authenication in @CanActivate.
    grouftyInjector(appRef.injector);
});