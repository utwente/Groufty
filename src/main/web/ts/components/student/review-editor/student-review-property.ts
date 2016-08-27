import {ReviewTemplateProperty} from "../../../model/review-templates/review-template-property";
import {ReviewProperty} from "../../../model/reviews/review-property";

export abstract class StudentReviewProperty {

    public editable : boolean;
    public hint : boolean;
    public grade : boolean;
    public id : number;
    public property : ReviewTemplateProperty;
    private _internalValue : ReviewProperty;

    public get value(): ReviewProperty {
        return this._internalValue;
    }

    public set value(value : ReviewProperty) {
        this._internalValue = value;
        if (this._internalValue) {
            this.setValue(this._internalValue);
        }
    }

    /**
     * Get the weight of the question.
     */
    public getWeight(): number {
        return this.property.weight;
    }

    /**
     * Function called when a new value is set for the property.
     * @param value The new value for the property.
     */
    abstract setValue(value : ReviewProperty) : void;

    /**
     * Return the current value of the property.
     */
    abstract getValue() : ReviewProperty;

    /**
     * Return if the current value of the property is valid.
     */
    abstract isValid() : boolean;

    /**
     * Return if the current value of the property is ready for submission.
     */
    abstract isReady() : boolean;

    /**
     * Get the points of this question.
     */
    abstract getPoints(): number;

}