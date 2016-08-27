import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Http, Headers, RequestOptions} from "@angular/http";
import {User} from "../model/users/user";
import {Page} from "../model/utils/page";
import {GrouftyHTTPService} from "./http.service";
import {AbstractService} from "./abstract.service";

/**
 * Class that provide the user services.
 * @author Javalon Development Team
 * @version 0.1
 */
@Injectable()
export class UserService extends AbstractService{
    private _usersUrl = GrouftyHTTPService.getAPI() + '/users';

    constructor (http: Http) {
        super(http);
    }

    
    /**
     * Get a list of users.
     * @param size The size of the users on the list.
     * @param page The page to get.
     * @returns {Observable<R>} The observable object for the request.
     */
    public getUsers(size : number = null, page : number = null) : Observable<Page<User>>{
        return super.getPage<User>(this._usersUrl, size, page);
    }

    /**
     * Get the user with the specified user id.
     * @param id The id of the user.
     * @returns {Observable<R>} The observable object for the request.
     */
    public getUser(id : number) : Observable<User> {
        let url = this._usersUrl + '/' + id;
        return super.performGet<User>(url);
    }

    /**
     * Add a specified user.
     * @param user The user object representing the user to add.
     * @returns {Observable<R>} The observable object for the request.
     */
    public addUser(user : User) : Observable<User> {
        let body = JSON.stringify(user);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return super.getHttp().post(this._usersUrl, body, options)
            .map(res =>  <User> res.json())
            .catch(super.handleError)
    }

    /**
     * Update the specified user.
     * @param user The user to update, containing the new information.
     * @returns {Observable<Response>} The observable object for the request.
     */
    public updateUser(user : User) : Observable<User>  {
        let body = JSON.stringify(user);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.getHttp().put(this._usersUrl + '/' + user.userId, body, options)
            .catch(super.handleError)
    }

    /**
     * Delete the specified user.
     * @param user The user to delete.
     * @returns {Observable<Response>} The observable object for the request.
     */
    public deleteUser(user : User) : Observable<User>  {
        return super.getHttp().delete(this._usersUrl + '/' + user.userId)
            .catch(super.handleError)
    }

}