import {NgClass} from "@angular/common";
import {Component} from "@angular/core";
import {Router, RouterLink} from "@angular/router-deprecated";
import {SessionService} from "../../services/session.service";
import {User, Role} from "../../model/users/user";

@Component({
    selector: 'nav',
    templateUrl: './templates/general/navigation.component.html',
    directives: [RouterLink, NgClass],
    inputs: ["router"]
})
export class NavigationComponent {

    public url: string;
    public session : User = null;
    public expanded : boolean = false;

    private _sessionSubscription : any;

    public constructor(private _router: Router,
                       private _sessionService : SessionService) {
        this.url = "";
        this._router.subscribe((url) => {
            this.url = url;
        });
        this._sessionService.resolveUser().then(user => this.session = user);
        this._sessionSubscription = this._sessionService.onChange.subscribe(user => this.session = user);
    }

    public logout() : void {
        this.closeMenu();
        this._sessionService.logout(() => {
            this._router.navigate(['Start']);
        });
    }

    public hasRole(role: Role): boolean {
        return this.session.authority == role;
    }

    public isPage(page: string): boolean {
        return this.url.indexOf(page) == 0;
    }

    public closeMenu() : void {
        this.expanded = false;
    }

    public toggleMenu(): void {
        this.expanded = !this.expanded;
    }

    public ngOnDestroy() : void {
        this._sessionSubscription.unsubscribe();
    }
}