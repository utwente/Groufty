import {Component} from "@angular/core";
import {RouterLink} from "@angular/router-deprecated";
import {ReleaseInfoService} from "../../services/release-info.service";
import {ReleaseInfo} from "../../model/release/release-info";
import {MomentPipe} from "../../utils/moment.pipe";

@Component({
    selector: 'footer',
    templateUrl: './templates/general/footer.component.html',
    directives: [RouterLink],
    providers: [ReleaseInfoService],
    pipes: [MomentPipe]
})
export class FooterComponent {

    public relInfo : ReleaseInfo = new ReleaseInfo();

    public constructor(private _releaseInfoService : ReleaseInfoService) {
        this._releaseInfoService.getReleaseInfo().subscribe(relInfo => this.relInfo = relInfo);
    }
    
}