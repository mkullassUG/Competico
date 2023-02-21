const MessagesModule = (function(deps={}) {
	/* environment preparation */
    var debug = false;
    if ( deps.debug )
        debug = true;

    if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
        throw Error("jQuery not defined");
    else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
        deps.$ = $;

    if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
        throw Error("window not defined");
    else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
        deps.window = window;
	
	var Ajax = function(){
        var self = {};

        self.getAccountInfo = (callback) => {
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/account/info",
                contentType: "application/json",
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("getAccountInfo success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getAccountInfo error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.getJoinRequestsCount = (callback) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/joinrequests/count",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getUnreadMessagesCount = (callback) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/messages/unread/count",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getUnreadMessagesCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getUnreadMessagesCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getUnreadMessages = (callback) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/messages/unread",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getUnreadMessages success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getUnreadMessages error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupLobbiesFrom = (code, callback) => {
                
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupLobbies = (callback) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupLobbies success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupLobbies error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }
		return self;
    }();
	
	
	const MessagesLogic = (data, successfulCreationCallback) => {
        
        /*  singleton   */
        if (MessagesLogic.singleton)
            return MessagesLogic.singleton;
        var self = {};
        if (!MessagesLogic.singleton && data)
            MessagesLogic.singleton = self;
        else if (!MessagesLogic.singleton && !data) 
            return MessagesLogic.getInstance(null, successfulCreationCallback);

        /*       logic variables          */
        self.givenFunc;
        self.delay = 5000;
        self.prevNumberOfMessages;
        self.prevNumberOfRequests;
        self.prevNumberOfLobbies;
        self.currNumberOfMessages;
        self.currNumberOfRequests;
        self.currNumberOfLobbies;
        self.lobbies;
        self.timeOut;
        self.isLecturer;
        self.firstFunctionFire = true;
        /*       logic functions          */
        var MessagesLogicInit = (initData) => {
            
            self.isLecturer = initData.accountInfo.roles.includes("LECTURER");
            self.prevNumberOfMessages = initData.messagesCount;
            self.prevNumberOfLobbies = initData.lobbies.length;
            self.lobbies = initData.lobbies;
            if ( self.isLecturer )
                self.prevNumberOfRequests = initData.requestsCount;

            if ( !window.localStorage.audio )

            self.isAudioOn = deps.window.localStorage.audio
            loopFunction();
            if (successfulCreationCallback)
                successfulCreationCallback(self);
		}

        self.setFunctionToInform = (givenFunc) => self.givenFunc = givenFunc;
        self.setDelay = (delay) => self.delay = delay;

        var loopFunction = ( ) => {

            //check for change
            var promiseArray = [
                Ajax.getUnreadMessagesCount(),
                Ajax.getGroupLobbies()
            ];

            if ( self.isLecturer )
                promiseArray.push(Ajax.getJoinRequestsCount());
            
            Promise.all(promiseArray)
                .then(data=>{
                    self.currNumberOfMessages = data[0];
                    self.currNumberOfLobbies = data[1].length;
                    self.lobbies = data[1];

                    if ( self.isLecturer )
                        self.currNumberOfRequests = data[2];

                    var sum = self.currNumberOfMessages + self.currNumberOfLobbies + (self.currNumberOfRequests?self.currNumberOfRequests:0);
                    
                    if ( typeof sum === 'string')
                        return;

                    if ( sum > 0) {
                        changeIcon("/assets/myIcons/CompeticoLogoMessage.svg");
                    } else {
                        changeIcon("/assets/myIcons/CompeticoLogo.svg");
                    }

                    if ( audioPlay ) {
                        updateAudioSettings();
                        if ( checkForChangeIsMore( )) {

                            var currNumberOfRequestsCheck = self.currNumberOfRequests;
                            if ( typeof currNumberOfRequestsCheck === 'undefined')
                                currNumberOfRequestsCheck = 'undefined';

                            if (!(deps.window.localStorage.NumberOfMessages == self.currNumberOfMessages)
                            || !(deps.window.localStorage.NumberOfLobbies == self.currNumberOfLobbies)
                            || !(deps.window.localStorage.NumberOfRequests == currNumberOfRequestsCheck)) {
    
                                deps.window.localStorage.NumberOfMessages = self.currNumberOfMessages;
                                deps.window.localStorage.NumberOfLobbies = self.currNumberOfLobbies;
                                deps.window.localStorage.NumberOfRequests = self.currNumberOfRequests;
                                audioPlay();
                            }
                        } 
                    }

                    if ( (checkForChange() && self.givenFunc) || self.firstFunctionFire) {
                        
                        deps.window.localStorage.NumberOfMessages = self.currNumberOfMessages;
                        deps.window.localStorage.NumberOfLobbies = self.currNumberOfLobbies;
                        deps.window.localStorage.NumberOfRequests = self.currNumberOfRequests;

                        var send = {
                            areNew: self.firstFunctionFire?false:checkForChangeIsMore(),
                            numberOfMessages: self.currNumberOfMessages,
                            numberOfLobbies: self.currNumberOfLobbies,
                            lobbies: self.lobbies
                        }
                        self.firstFunctionFire = false;
                        if ( self.isLecturer )
                            send.numberOfRequests = self.currNumberOfRequests;

                        self.givenFunc(send);

                        self.prevNumberOfMessages = self.currNumberOfMessages;
                        self.prevNumberOfLobbies = self.currNumberOfLobbies;
                        if ( self.isLecturer )
                            self.prevNumberOfRequests = self.currNumberOfRequests;
                        
                    }

                }).catch(e=>{
                    if ( debug )
                        console.warn(e);
                })

            self.timeOut = setTimeout(
                function(){loopFunction();},
                self.delay
            )
        }

        var checkForChange = () => {

            if ( self.prevNumberOfMessages !== self.currNumberOfMessages)
                return true;
            if ( self.prevNumberOfRequests !== self.currNumberOfRequests)
                return true;
            if ( self.prevNumberOfLobbies !== self.currNumberOfLobbies)
                return true;
            
            return false;
        }

        var checkForChangeIsMore = () => {

            if ( self.prevNumberOfMessages < self.currNumberOfMessages)
                return true;
            if ( self.prevNumberOfRequests < self.currNumberOfRequests)
                return true;
            if ( self.prevNumberOfLobbies < self.currNumberOfLobbies)
                return true;
            
            return false;
        }

        var changeIcon = (newURL) => {
            var link = deps.window.document.querySelector("link[rel~='icon']");
            link.href = newURL;
        }

        /*audio*/
        var setupAudio = () => {
            self.isAudioOn = false;

            updateAudioSettings();
            audioPlay = () => {
                if ( !self.isAudioOn )
                    return;
                    
                try {
                    var a = new Audio("/mp3/message.mp3");
                    a.volume = self.audioVol;
                    a.play();
                } catch(e) {

                }
            }
        }

        var updateAudioSettings = () => {

            if ( !deps.window.localStorage.audio || deps.window.localStorage.audio == "on") {
                deps.window.localStorage.audio = "on";
                deps.$("#audioBtn i").addClass("fa-volume-up").removeClass("fa-volume-off");
                self.audioVol = 0.3;
                self.isAudioOn = true;

            } else {
                self.audioVol = 0;
                self.isAudioOn = false;
                deps.$("#audioBtn i").removeClass("fa-volume-up").addClass("fa-volume-off");
            }
        }

        var audioSwitch = () => {
            if ( self.isAudioOn ) {
                
                deps.$("#audioBtn i").removeClass("fa-volume-up").addClass("fa-volume-off");
                deps.window.localStorage.audio = "off";
                self.audioVol = 0;
                self.isAudioOn = false;
            } else {
                deps.$("#audioBtn i").addClass("fa-volume-up").removeClass("fa-volume-off");
                deps.window.localStorage.audio = "on";
                self.audioVol = 0.3;
                self.isAudioOn = true;
            }
        }
        var audioPlay;

        /*       event listeners          */
        if ( deps.$("#audioBtn").length ) {
            setupAudio();
            deps.$("#audioBtn").on('click', (e) => {
                audioSwitch();
            });
        }
        /*  	 initalization  		  */
        MessagesLogicInit(data);
         
        return self;
    }
	MessagesLogic.getInstance = (dataLazy, successfulCreationCallback) => {
        
        if (MessagesLogic.singleton)
            return MessagesLogic.singleton;

            if ( dataLazy )
            return MessagesLogic(dataLazy, successfulCreationCallback);
        else {
            return Promise.all([
                Ajax.getAccountInfo(),
                Ajax.getUnreadMessagesCount(),
                Ajax.getGroupLobbies(),
            ]).then((values)=>{
                
                var data = {
                    accountInfo: values[0],
                    messagesCount: values[1],
                    lobbies: values[2],
                }

                if ( data.accountInfo.roles.includes("LECTURER") ) {

                    return Promise.all([
                        Ajax.getJoinRequestsCount()
                    ]).then((values)=>{
                        
                        data.requestsCount = values[0];

                        MessagesLogic.singleton = MessagesLogic(data, successfulCreationCallback);
                        return MessagesLogic.singleton;
                    }).catch((e) => {
                        if ( debug )
                            console.log('\x1b[31m%s\x1b[0m', 'failed to download "join requests" ');
                        console.warn(e);
                        if ( successfulCreationCallback )
                            successfulCreationCallback(false);
                            
                    });
                } else {
                    MessagesLogic.singleton = MessagesLogic(data, successfulCreationCallback);
                    return MessagesLogic.singleton;
                }
            }).catch((e) => {
                if ( debug )
                    console.log('\x1b[31m%s\x1b[0m', 'failed to download "account info, unread messages count, group lobbies "');
                console.warn(e);
                if ( successfulCreationCallback )
                    successfulCreationCallback(false);
                // all requests finished but one or more failed
            });
        }
	}

	return {
        MessagesLogic: MessagesLogic,
        getInstance: MessagesLogic.getInstance
    }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {MessagesModule};