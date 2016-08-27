export class SessionCredentials {
    public userId: string;
    public password : string;

    public static construct(userId: string, password: string): SessionCredentials {
        let creds = new SessionCredentials();
        creds.userId = userId;
        creds.password = password;
        return creds;
    }
}