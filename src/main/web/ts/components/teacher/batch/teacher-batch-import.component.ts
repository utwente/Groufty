import {Component} from "@angular/core";
import {FileUploadComponent} from "../../utils/file-upload.component";
import {MessagesComponent} from "../../utils/messages.component";
import {GrouftyFileDescriptor} from "../../../utils/file-upload/descriptor.file-upload";
import {BatchExportComponent} from "../../utils/batch-export.component";

@Component({
    templateUrl: './templates/teacher/batch/teacher-batch-import.component.html',
	directives: [FileUploadComponent, MessagesComponent, BatchExportComponent],

})
export class TeacherBatchImportComponent {

	public descriptors : Array<GrouftyFileDescriptor>;

	public constructor() {
		this.descriptors = new Array<GrouftyFileDescriptor>(11);
		this.descriptors[0] = new GrouftyFileDescriptor("users", "User import file", 0, 1, 10000*1024);
		this.descriptors[1] = new GrouftyFileDescriptor("groupings", "Groupings import file", 0, 1, 10000*1024);
		this.descriptors[2] = new GrouftyFileDescriptor("groups", "Groups import file", 0, 1, 10000*1024);
		this.descriptors[3] = new GrouftyFileDescriptor("reviewTemplates", "Review templates import file", 0, 100, 10000*1024);
		this.descriptors[4] = new GrouftyFileDescriptor("taskLists", "Task list import file", 0, 1, 10000*1024);
		this.descriptors[5] = new GrouftyFileDescriptor("tasks", "Tasks import file", 0, 1, 10000*1024);
		this.descriptors[6] = new GrouftyFileDescriptor("submissions", "Submissions import file", 0, 1,  10000*1024);
		this.descriptors[7] = new GrouftyFileDescriptor("submissionLists", "Submission lists import file", 0, 1,  10000*1024);
		this.descriptors[8] = new GrouftyFileDescriptor("submissionFiles", "Submission files import file", 0, 100,  10000*1024);
		this.descriptors[9] = new GrouftyFileDescriptor("taskFiles", "Task files import file", 0, 100,  10000*1024);
		this.descriptors[10] = new GrouftyFileDescriptor("reviews", "Reviews import file", 0, 100,  10000*1024);
	}

}