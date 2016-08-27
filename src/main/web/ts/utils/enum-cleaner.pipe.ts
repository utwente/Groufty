import {Pipe} from "@angular/core";

/***
 * This simple pipe 'cleans' enum values by removing any
 * underscores and transforming the given string to lowercase
 */
@Pipe({
    name: "enumCleaner"
})
export class EnumCleanerPipe {
    public transform(value) {

        if (value != null) {
            //This line will replace all underscore with spaces, and then convert everything to
            // lowercase except every first letter of a word.
            return value.replace('_', ' ').replace(/\B([A-Z]+)/g, (str) => {
                return str.toLowerCase();
            });
        } else {
            return "ENUM ERR";
        }

    }
}