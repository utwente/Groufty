export type MessageType = "danger" | "warning" | "primary" | "success" | "info";

export class Message {
    public constructor(public content : string, public type : MessageType,
                       public closable : boolean = true, public timeout : number = undefined) {}
}