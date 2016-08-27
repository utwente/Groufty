import {ReviewDTO} from "./review-dto";
import {Flag} from "./flag";

/**
 * This class is used in the feedback section where it is returned by the task-detail API and gives a
 * more detailed review for viewing than review-detail
 */
export class Review extends ReviewDTO {
    public flag : Flag;
    public lastEdited : number;
    public reviewId : number;
}