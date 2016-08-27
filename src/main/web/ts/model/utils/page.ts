export class Page<T> {
    public content : T[];
    public first : boolean;
    public last : boolean;
    public number : number;
    public numberOfElements : number;
    public size : number;
    //public sort : Sort;
    public totalElements : number;
    public totalPages : number;
}