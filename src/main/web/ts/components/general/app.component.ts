/// <reference path="../../../typings/index.d.ts" />

import {Component} from "@angular/core";
import {RouteConfig, ROUTER_DIRECTIVES, AsyncRoute} from "@angular/router-deprecated";
import {UserService} from "../../services/user.service";
import {NavigationComponent} from "./navigation.component";
import {RedirectComponent} from "./redirect.component";
import {GrouftyHTTPService} from "../../services/http.service";
import {FooterComponent} from "./footer.component";

@Component({
    selector: '#groufty-app',
    templateUrl: './templates/general/app.component.html',
    directives: [ROUTER_DIRECTIVES, NavigationComponent, FooterComponent],
    providers: [UserService]
})
@RouteConfig([
    { path: '/', name: 'Start', component: RedirectComponent, useAsDefault: true },
    new AsyncRoute({
        path: '/security',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/general/security.component')
            .then(m => m.SecurityComponent),
        name: 'Security'
    }),
    new AsyncRoute({
        path: '/login',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/general/login.component')
            .then(m => m.LoginComponent),
        name: 'Login'
    }),
    new AsyncRoute({
        path: '/tasks/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/student/tasks/student-tasks-router.component')
            .then(m => m.StudentTasksRouterComponent),
        name: 'StudentTasks'
    }),
    new AsyncRoute({
        path: '/reviews/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/student/reviews/student-reviews-router.component')
            .then(m => m.StudentReviewsRouterComponent),
        name: 'StudentReviews'
    }),
    new AsyncRoute({
        path: '/feedback/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/student/feedback/student-feedback-router.component')
            .then(m => m.StudentFeedbackRouterComponent),
        name: 'StudentFeedback'
    }),
    new AsyncRoute({
        path: '/batch/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/batch/teacher-batch-router.component')
            .then(m => m.TeacherBatchRouterComponent),
        name: 'TeacherBatch'
    }),
    new AsyncRoute({
        path: '/templates/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/review-template/teacher-review-template-router.component')
            .then(m => m.TeacherReviewTemplateRouterComponent),
        name: 'TeacherReviewTemplate'
    }),
    new AsyncRoute({
        path: '/submission/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/submission/teacher-submission-router.component')
            .then(m => m.TeacherSubmissionRouterComponent),
        name: 'TeacherSubmission'
    }),
    new AsyncRoute({
        path: '/submission-lists/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/submission-lists/teacher-submission-lists-router.component')
            .then(m => m.TeacherSubmissionListsRouterComponent),
        name: 'TeacherSubmissionLists'
    }),
    new AsyncRoute({
        path: '/task-lists/...',
        loader: () => System.import(GrouftyHTTPService.getJS() + '/components/teacher/task-lists/teacher-task-lists-router.component')
            .then(m => m.TeacherTaskListsRouterComponent),
        name: 'TeacherTaskLists'
    })
])
export class AppComponent {

    public showNavigation : boolean = true;

}