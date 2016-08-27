export type Role = "ROLE_NONE" | "ROLE_PARTICIPANT" | "ROLE_EDITOR";

export class User {
    public fullName : string;
    public userId : number;
    public authorId : number;
    public authority: Role;
    public enabled: boolean;
}