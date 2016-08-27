/// <reference path="../../typings/index.d.ts" />

import {Injectable} from "@angular/core";

/**
 * Service that provides generic utils for all REST based services.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class GrouftyHTTPService {

    public timestamp(): number {
        return moment.utc().valueOf();
    }

    public noCache(url: string): string {
        return url + '?' + this.timestamp();
    }

    public static getAPI(): string {
        return "api/v1";
    }

    public static getJS(): string {
        return "js";
    }
}