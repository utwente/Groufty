import {ReviewProperty} from "./review-property";

/**
 * This class is used in the feedback section where it is returned by the task-detail API and gives a
 * more detailed review for viewing than review-detail
 */
export class ReviewDTO {
    public authorId : number;
    public submitted : boolean;
    public reviewProperties : ReviewProperty[];

    public static dtoFromReview(review : ReviewDTO) {
        let dto = new ReviewDTO();
        dto.authorId = review.authorId;
        dto.submitted = review.submitted;
        dto.reviewProperties = review.reviewProperties;
        return dto;
    }
}