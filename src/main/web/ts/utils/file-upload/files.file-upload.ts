import {GrouftyFileDescriptor} from "./descriptor.file-upload";

export class GrouftyFiles extends GrouftyFileDescriptor {

    public files: File[] = [];
    public feedback: boolean = false;

    public constructor(
        public alias : string,
        public description : string = "",
        public minAmount : number = 1,
        public maxAmount : number = 1,
        public maxFileSize : number = 0,
        public mimeRestriction : string[] = []) {
        super(alias, description, minAmount, maxAmount, maxFileSize, mimeRestriction);
    }

    public isValid(index: number = -1): boolean {
        if (index >= 0) {
            return this._checkFile(index);
        } else {
            let result = this.files.length >= this.minAmount && this.files.length <= this.maxAmount;
            for (let i = 0; i < this.files.length && result; i++) {
                result = result && this._checkFile(i);
            }
            return result;
        }
    }

    private _checkFile(i: number) {
        return this._checkValid(i) && this._checkSize(i) && this._checkMime(i);
    }

    private _checkValid(i: number) {
        return typeof this.files[i].size === 'number';
    }

    private _checkSize(i: number) {
        return this.maxFileSize == 0 || this.files[i].size <= this.maxFileSize;
    }

    private _checkMime(i: number) {
        if (typeof this.files[i].type === 'string' && this.files[i].type != "" && this.mimeRestriction.length > 0) {
            for (let i = 0; i < this.mimeRestriction.length; i++) {
                if (this.mimeRestriction[i] == this.files[i].type) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static fromDescriptor(descriptor: GrouftyFileDescriptor) {
        return new GrouftyFiles(descriptor.alias, descriptor.description, descriptor.minAmount,
            descriptor.maxAmount, descriptor.maxFileSize, descriptor.mimeRestriction);
    }

}