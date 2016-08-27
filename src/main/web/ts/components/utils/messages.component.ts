import {Input, Component} from "@angular/core";
import {Message} from "../../model/utils/message";
import {AlertComponent} from "./alert.component";

@Component({
    selector: 'messages',
    templateUrl: './templates/utils/messages.component.html',
    directives: [AlertComponent]
})
export class MessagesComponent {

    @Input() public messages : Message[];

    public removeMessage(index : number) {
        this.messages.splice(index, 1);
    }

}