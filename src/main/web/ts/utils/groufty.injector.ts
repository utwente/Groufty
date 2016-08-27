import {Injector} from "@angular/core";

let grouftyInjectorRef: Injector;

export const grouftyInjector = (injector?: Injector):Injector => {
    if (injector) {
        grouftyInjectorRef = injector;
    }
    return grouftyInjectorRef;
};