export namespace main {
	
	export class Capture {
	    dataURL: string;
	    x: number;
	    y: number;
	    width: number;
	    height: number;
	
	    static createFrom(source: any = {}) {
	        return new Capture(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.dataURL = source["dataURL"];
	        this.x = source["x"];
	        this.y = source["y"];
	        this.width = source["width"];
	        this.height = source["height"];
	    }
	}
	export class Settings {
	    spellCheck: boolean;
	    textWrap: boolean;
	    textSize: number;
	    textFontFamily: string;
	    appThemeColor: string;
	    textFontColor: string;
	
	    static createFrom(source: any = {}) {
	        return new Settings(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.spellCheck = source["spellCheck"];
	        this.textWrap = source["textWrap"];
	        this.textSize = source["textSize"];
	        this.textFontFamily = source["textFontFamily"];
	        this.appThemeColor = source["appThemeColor"];
	        this.textFontColor = source["textFontColor"];
	    }
	}
	export class WindowSettings {
	    width: number;
	    height: number;
	    posX?: number;
	    posY?: number;
	    opacity: number;
	    mouseHoverOpacity: boolean;
	
	    static createFrom(source: any = {}) {
	        return new WindowSettings(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.width = source["width"];
	        this.height = source["height"];
	        this.posX = source["posX"];
	        this.posY = source["posY"];
	        this.opacity = source["opacity"];
	        this.mouseHoverOpacity = source["mouseHoverOpacity"];
	    }
	}
	export class State {
	    settings: Settings;
	    windowSettings: WindowSettings;
	    content: string;
	
	    static createFrom(source: any = {}) {
	        return new State(source);
	    }
	
	    constructor(source: any = {}) {
	        if ('string' === typeof source) source = JSON.parse(source);
	        this.settings = this.convertValues(source["settings"], Settings);
	        this.windowSettings = this.convertValues(source["windowSettings"], WindowSettings);
	        this.content = source["content"];
	    }
	
		convertValues(a: any, classs: any, asMap: boolean = false): any {
		    if (!a) {
		        return a;
		    }
		    if (a.slice && a.map) {
		        return (a as any[]).map(elem => this.convertValues(elem, classs));
		    } else if ("object" === typeof a) {
		        if (asMap) {
		            for (const key of Object.keys(a)) {
		                a[key] = new classs(a[key]);
		            }
		            return a;
		        }
		        return new classs(a);
		    }
		    return a;
		}
	}

}

