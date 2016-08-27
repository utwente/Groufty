export class GrouftyFileDescriptor {

    public constructor(
        public alias : string,
        public description : string = "",
        public minAmount : number = 1,
        public maxAmount : number = 1,
        public maxFileSize : number = 0,
        public mimeRestriction : string[] = []) {}

}