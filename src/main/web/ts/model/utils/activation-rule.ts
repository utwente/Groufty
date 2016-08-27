import {Role} from "../users/user";

export type RequiredRole = "ROLE_ANY" | "ROLE_NONE" | "ROLE_PARTICIPANT" | "ROLE_EDITOR";

export class ActivationRule {
    
    public requireRole: RequiredRole = "ROLE_ANY";
    public requireSession: boolean = false;
    public page: string = null;
    
    public static activateOnSession(page: string = null) {
        let result = new ActivationRule();
        result.requireSession = true;
        result.page = page;
        return result;
    }

    public static activateOnRole(role: RequiredRole, page: string = null) {
        let result = new ActivationRule();
        result.requireRole = role;
        result.requireSession = true;
        result.page = page;
        return result;
    }

    public match(session: boolean, role: Role): boolean {
        return (this.requireSession == false || session == true) &&
            (this.requireRole == "ROLE_ANY" || this.requireRole == role);
    }
    
}