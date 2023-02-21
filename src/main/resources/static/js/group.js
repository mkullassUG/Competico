const GroupModule = (function(deps={}) {

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
    
    if ( typeof NavbarLogic === 'undefined' && typeof deps.NavbarLogic === 'undefined') 
        throw Error("NavbarLogic not defined");
    if ( typeof deps.NavbarLogic === "undefined" && typeof NavbarLogic != "undefined")
        deps.NavbarLogic = NavbarLogic;

        
    if ( typeof PageLanguageChanger === 'undefined' && typeof deps.PageLanguageChanger === 'undefined') 
        throw new Error("PageLanguageChanger not defined");
    if ( typeof deps.PageLanguageChanger === "undefined" && typeof PageLanguageChanger != "undefined")
        deps.PageLanguageChanger = PageLanguageChanger;
    
    var Ajax = function(){
        var self = {};

        self.getWhoAmI = ( callback ) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/playerinfo",
                contentType: "application/json",
                success: function(playerInfo, textStatus, jqXHR) {
                    if (debug){
                        console.log("getWhoAmI success");
                        console.log(playerInfo);
                    }
                    if ( callback )
                        callback(playerInfo);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("getWhoAmI error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getAccountInfo = (callback) => {
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/account/info",
                contentType: "application/json",
                success: function(accountInfo, textStatus, jqXHR) {
                    if (debug) {
                        console.log("getAccountInfo success");
                        console.log(accountInfo);
                    }
                    if ( callback )
                        callback(accountInfo);
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

        self.getGroupLobbiesFrom = (code, callback) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupLobbiesFrom success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupLobbiesFrom error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postCreateGroupLobby = (code, callback) => {

            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/lobby",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postCreateGroupLobby success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postCreateGroupLobby error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }
        self.postSendMessage = (title, content, code, callback) => {

            var send = JSON.stringify({title:title,content:content});
            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/messages",
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postSendMessage success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postSendMessage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }
        
        self.deleteGroup = (code, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/groups/"+code,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("deleteGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("deleteGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postLeaveGroup = (code, callback) => {

            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/" + code + "/leave",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postLeaveGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postLeaveGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }
        
        
        self.putGroupName = (newGroupName, code, callback) => {

            var send = JSON.stringify({newGroupName:newGroupName});
            return deps.$.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/name",
                contentType: "application/json",
                data     : send,
                success: function(pageInfo, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("putGroupName success");
                        console.log(pageInfo);
                    }
                    if ( callback )
                        callback(pageInfo);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("putGroupName error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupInfo = (code, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/info",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupInfo success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupInfo error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.getGroupNames = (callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/names",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupNames success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupNames error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.deleteGroup = (code, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/groups/" + code,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("deleteGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("deleteGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.deleteUserFromGroup = (username, code, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/user/"+username,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("deleteUserFromGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("deleteUserFromGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.getGroupRequests = (code, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/requests",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupInfo success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupInfo error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postJoinRequest = (id, accept, callback) => {
            
            var send = JSON.stringify({accept:accept});

            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/requests/"+id+"/respond",
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postJoinRequest success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postJoinRequest error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }

        self.deleteUserFromGroup = (code, username, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/user/" + username,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("deleteUserFromGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("deleteUserFromGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupHistoryPage = (code, historyPage, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/game/history/"+historyPage,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupInfo success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupInfo error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getMessagesFor = ( code, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/messages",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getMessagesFor success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getMessagesFor error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postReadMessage = (id, callback) => {

            var send = JSON.stringify({messageRead:true});
            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/message/"+id+"/read",
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postReadMessage success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postReadMessage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }


        self.putUpdateMessage = (title, content, id, callback) => {

            var send = JSON.stringify({title:title,content:content});
            return deps.$.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/groups/message/" + id,
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("putUpdateMessage success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("putUpdateMessage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }
        
        return self;
    }();

    const GroupLogic = (data, successfulCreationCallback) => {

        /*  singleton   */
        if (GroupLogic.singleton)
            return GroupLogic.singleton;
        var self = {};
        if (!GroupLogic.singleton && data)
            GroupLogic.singleton = self;
        else if (!GroupLogic.singleton && !data) 
            return GroupLogic.getInstance(null, successfulCreationCallback);
    
        /*       logic variables          */
        self.playerInfo = data.account;
        self.debug = debug;
        self.isLecturer;
        self.groupData = data.groupData;
        self.groupRequests = data.groupRequests;
        self.groupMessages = data.groupMessages;
        self.groupHistory = data.groupHistory;
        var groupNameRegex = /^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:"'`!@#$%^&*()[\]{}_+=|\\\-]{2,32}$/;
        self.currentNewGroupName;
        self.groupNames;
        self.groupLobbies = data.groupLobbies;
        
        /*       logic functions          */
        var GroupLogicInit = (initData) => {
            
            deps.PageLanguageChanger(false, debug, deps, self.InitWithPageLanguageChanger);

            var roles = self.playerInfo.accountInfo.roles?self.playerInfo.accountInfo.roles:[];

            if ( roles.includes("LECTURER") ) {
                setupLecturerPage();
            } else {
                setupPlayerPage();
            }

            setGroupInfo(self.groupData);
            tooltipsUpdate(); 
    
            deps.NavbarLogic(self.playerInfo.accountInfo, debug, deps);
    
            listenersSetup();
            
            if ( deps.window.location.hash.includes("#historyPage=")) {
                self.switchToView("History");
                self.setupHistoryPagi();
            } else { 
                self.cancelHistoryPagi();
            }

            if ( typeof MessagesModule !== undefined)
                MessagesModule().getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});
            if (successfulCreationCallback)
                successfulCreationCallback(self);
        }
        
        self.InitWithPageLanguageChanger = (data, lang) => {}

        /* messages */
        self.messagerFunction = (data) => {

            if ( data.areNew ) {
                //dźwiek
                //console.log("Ding!")

            }

            var updatePage = () => {

                var requestArray = [
                    Ajax.getGroupInfo(self.groupData.groupCode),
                    Ajax.getGroupHistoryPage(self.groupData.groupCode, self.groupHistory.number + 1),
                    Ajax.getMessagesFor(self.groupData.groupCode),
                    Ajax.getGroupLobbiesFrom(self.groupData.groupCode),
                ];

                if ( self.isLecturer )
                    requestArray.push(Ajax.getGroupRequests(self.groupData.groupCode));

                Promise.all(requestArray).then((values)=>{
                
                    self.groupData = values[0];
                    self.groupHistory = values[1];
                    self.groupMessages = values[2];
                    self.groupLobbies = values[3];
                    
                    if ( self.isLecturer )
                        self.groupRequests = values[4];
                    
                    if ( self.isLecturer ) {
                        setupLecturerPage();
                    } else {
                        setupPlayerPage();
                    }
                }).catch(e=> {
                    if ( debug )
                        console.log(e);
                })
            }
            updatePage();
            
            var updateNavbar = () => {
                deps.$("#messageUnreadMessages").text(
                    (data.numberOfMessages+data.numberOfLobbies)?
                    ((data.numberOfMessages+data.numberOfLobbies)>99?
                    "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
                deps.$("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
            }
            updateNavbar();
        }

        self.currentlySwitchingFades = false;
        self.lastSwitchTo;
        self.switchToView = (mode) => {

            self.lastSwitchTo = mode;
            if (self.currentlySwitchingFades)
                return;
            self.lastSwitchTo = undefined;

            switch (mode) {
                case "Requests":
                    self.cancelHistoryPagi();
                    self.requestsViewActive = true;
                    self.historyViewActive = false;
                    self.manageViewActive = false;
                    self.messageViewActive = false;
                    deps.$("#btnMessageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnManageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnInfoView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnHistoryView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnRequestView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;
                    deps.$("#historyDiv").hide();
                    deps.$("#manageInfoDiv").hide();
                    deps.$("#messageDiv").hide();
                    deps.$("#requestsDiv").fadeIn().promise().done(function(){
                        self.currentlySwitchingFades = false;
                        if ( self.lastSwitchTo )
                            self.switchToView(self.lastSwitchTo);
                    });
                    break;
                case "History":
                    self.setupHistoryPagi();
                    self.requestsViewActive = false;
                    self.historyViewActive = true;
                    self.manageViewActive = false;
                    self.messageViewActive = false;
                    deps.$("#btnMessageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnManageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnInfoView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnHistoryView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;
                    deps.$("#requestsDiv").hide();
                    deps.$("#manageInfoDiv").hide();
                    deps.$("#messageDiv").hide();
                    deps.$("#historyDiv").fadeIn().promise().done(function(){
                        self.currentlySwitchingFades = false;
                        if ( self.lastSwitchTo )
                            self.switchToView(self.lastSwitchTo);
                    });
                    break;
                case "Message":
                    self.cancelHistoryPagi();
                    self.requestsViewActive = false;
                    self.historyViewActive = false;
                    self.manageViewActive = false;
                    self.messageViewActive = true;
                    deps.$("#btnHistoryView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnManageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnInfoView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnMessageView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;
                    deps.$("#requestsDiv").hide();
                    deps.$("#manageInfoDiv").hide();
                    deps.$("#historyDiv").hide();
                    deps.$("#messageDiv").fadeIn().promise().done(function(){
                        self.currentlySwitchingFades = false;
                        if ( self.lastSwitchTo )
                            self.switchToView(self.lastSwitchTo);
                    });
                    break;
                default:
                    self.cancelHistoryPagi();
                    self.requestsViewActive = false;
                    self.historyViewActive = false;
                    self.manageViewActive = true;
                    self.messageViewActive = false;
                    deps.$("#btnMessageView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnHistoryView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnManageView").addClass("btn-primary active").removeClass("btn-secondary");
                    deps.$("#btnInfoView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;
                    deps.$("#requestsDiv").hide();
                    deps.$("#historyDiv").hide();
                    deps.$("#messageDiv").hide();
                    deps.$("#manageInfoDiv").fadeIn().promise().done(function(){
                        self.currentlySwitchingFades = false;
                        if ( self.lastSwitchTo )
                            self.switchToView(self.lastSwitchTo);
                    });
            }
        }

        var setupLecturerPage = () => {
            self.isLecturer = true;

            setGroupInfo(self.groupData);

            var setupPlayersTableLecturer = () => {

                var groupTBody = deps.$("#groupTBody");
                deps.$("#memberCount").text(self.groupData.players.length);
                groupTBody.html('');
    
                for ( let i = 0; i < self.groupData.players.length; i++) {
                    var player = self.groupData.players[i];
    
                    var newTr = deps.$("<tr>"),
                    indexTd = deps.$("<td>").text(i+1),
                    nameTd = deps.$("<td>").text(player),
                    deleteTd = deps.$("<td>"),
                    profileTd = deps.$("<td>").append(deps.$(`<a href="/profile/`+player+`" class="playerProfile">
                    `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("ProfileBtn"):"Profil")+`
                    </a>`));
    
                    
                    var deleteBtn = $(`<button data-toggle="modal" data-target="#removeUserModal" aria-hidden="true">`)
                        .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("RemoveBtn"):"Wyproś"))
                        .addClass(["btn","btn-danger", "btn-sm"])
                        .on('click',e=>{
                            var target = $(e.target);
                            var user = target.closest("tr")[0].dataset["user"];
                            self.focusedManageDeleteUser = user;
                            deps.$("#removeGroupHeader").text(user)
                        });
                    deleteTd.append(deleteBtn);

                    newTr[0].dataset["user"] = player;

                    newTr.append(indexTd).append(nameTd).append(deleteTd).append(profileTd);
                    groupTBody.append(newTr);
                }
            }
            setupPlayersTableLecturer();
            deps.$("#btnManageView").show();
            deps.$("#btnInfoView").remove();
            deps.$("#btnRequestView").show();

            var setupRequestsTable = () => {

                var groupTBody = deps.$("#groupTBody2");
                groupTBody.html('');

                if ( !self.groupRequests.length )
                    groupTBody.html(`<div class="text-center noRequests"><div>`+
                        ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noRequests"):"Brak próśb")
                        +`</div></div>`);
                else
                    deps.$("#requestsCountInfo").text(self.groupRequests.length?self.groupRequests.length:"");
                
    
                for ( let i = 0; i < self.groupRequests.length; i++) {
                    var req = self.groupRequests[i];
                    
                    var type = req.roles.includes("LECTURER")?"Lektor":"Gracz";

                    var newTr = deps.$("<tr>"),
                    indexTd = deps.$("<td>").text(i+1),
                    nameTd = deps.$("<td>").text(req.username),
                    profileTd = deps.$("<td>").append(deps.$(`<a href="/profile/`+req.username+`" class="playerProfile">
                    `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("ProfileBtn"):"Profil")+`
                    </a>`)),
                    typeTd = deps.$("<td>").text(
                        ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("UserType"+type):type)
                        ),
                    creatTd = deps.$("<td>").text(req.creationDate),
                    acceptTd = deps.$("<td>"),
                    deleteTd = deps.$("<td>");
                    
                    
                    var acceptBtn = $(`<button data-toggle="modal" data-target="#addRequestModal" aria-hidden="true">`)
                        .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("AcceptBtn"):"Akceptuj"))
                        .addClass(["btn","btn-success","btn-sm"])
                        .on('click',e=>{
                            var target = $(e.target);
                            var id = target.closest("tr")[0].dataset["id"];
                            var user = target.closest("tr")[0].dataset["user"];
                            deps.$("#addRequestGroupHeader").text(user);
                            self.focusedHistoryRequest = id;
                        });
                    acceptTd.append(acceptBtn);
                    var declineBtn = $(`<button data-toggle="modal" data-target="#removeRequestModal" aria-hidden="true">`)
                        .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("DeleteBtn"):"Usuń"))
                        .addClass(["btn","btn-danger", "btn-sm"])
                        .on('click',e=>{
                            var target = $(e.target);
                            var id = target.closest("tr")[0].dataset["id"];
                            var user = target.closest("tr")[0].dataset["user"];
                            deps.$("#removeRequestGroupHeader").text(user);
                            self.focusedHistoryRequest = id;
                        });
                    deleteTd.append(declineBtn);


                    newTr[0].dataset["id"] = req.id;
                    newTr[0].dataset["user"] = req.username;
                    
                    newTr.append(indexTd).append(nameTd).append(profileTd).append(typeTd).append(creatTd).append(acceptTd).append(deleteTd);
                    groupTBody.append(newTr);
                }
            }
            setupRequestsTable();
            setupHistoryTable();
            setupMessagesTable();
        }   
        
        var setGroupInfo = (group) => {
            deps.$("#groupName").text(group.name);
            deps.$("#groupInfo1").text(group.creationDate)
            .prepend(deps.$(`<span class="trans1">
            `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor(".trans1", false, false):"Założono: ")+`
            </span>`)); 
            deps.$("#groupInfo2").text(group.name)
            .prepend(deps.$(`<span class="trans2">
            `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor(".trans2", false, false):"Nazwa: ")+`
            </span>`)); 
            deps.$("#groupInfo3").text(group.groupCode)
            .prepend(deps.$(`<span class="trans3">
            `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor(".trans3", false, false):"Kod: ")+`
            </span>`));  
            deps.$("#groupInfo4").text(group.lecturers.join(", "))
            .prepend(deps.$(`<span class="trans4">
            `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor(".trans4", false, false):"Lektorzy: ")+`
            </span>`)); 
        }

        self.firstSetup = true;
        var setupPlayerPage = () => {
            self.isLecturer = false;

            setGroupInfo(self.groupData);
            
            var setupPlayersTablePlayer = () => {

                if ( self.firstSetup  ) {
                    self.firstSetup = false;
                    deps.$("#btnMessageView").removeClass(["col-sm-3","col-6"]).addClass("col-4");
                    deps.$("#btnHistoryView").removeClass(["col-sm-3","col-6"]).addClass("col-4");
                    var holder = deps.$("#manageInfoDiv > .row:first-child");
                    var col1 = deps.$(`<div class="col-12 col-md-6 p-1 p-sm-2 p-md-4">`);
                    var col2 = deps.$(`<div class="col-12 col-md-6 p-1 p-sm-2 p-md-4">`);
                    var col3 = deps.$(`<div class="col-12 col-md-6 p-1 p-sm-2 p-md-4">`);
                    var col4 = deps.$(`<div class="col-12 col-md-6 p-1 p-sm-2 p-md-4">`);
                    holder.append(col1).append(col2).append(col3).append(col4);

                    col1.append(deps.$(`#groupInfo1`));
                    col2.append(deps.$(`#groupInfo2`));
                    col3.append(deps.$(`#groupInfo3`)).append(deps.$(`#btnCopyCode`).parent());
                    
                    col4.append(deps.$(`#groupInfo4`));
                    deps.$("#manageInfoDiv > .row:nth-child(2)").remove();
                    deps.$("#manageInfoDiv > .row:nth-child(3)").remove();
                    deps.$("#manageInfoDiv > .row:nth-child(4)").remove();

                    deps.$("#manageInfoDiv > table > thead > tr > th:nth-child(4)").hide();
                    deps.$("#manageInfoDiv > table > thead > tr > th:nth-child(3)").hide();
                    deps.$("#btnStartGroupLobbyModal").hide();
                    deps.$("#btnDeleteGroupModal").hide();
                    deps.$("#btnSendMessageModal").hide();
                    deps.$("#btnChangeNameModal").closest("div").remove();

                    deps.$("#btnCopyCode").closest("div.col-12").hover( 
                        (e)=>{
                        // handlerIn
                        self.btnCopyCodeModalBtnFadingLast = 1;
                        if ( !self.btnCopyCodeModalBtnFading ){
                            self.btnCopyCodeModalBtnFadingLast = undefined;
                            self.btnCopyCodeModalBtnFading = true;
                            deps.$("#btnCopyCode").fadeIn().promise().done(function(){
                                self.btnCopyCodeModalBtnFading = false;
    
                                if ( self.btnCopyCodeModalBtnFadingLast == 1 )
                                    deps.$("#btnCopyCode").fadeIn();
                                else if (self.btnCopyCodeModalBtnFadingLast == 2 )
                                    deps.$("#btnCopyCode").fadeOut();
                            });
                        }
                    },  (e)=>{
                        // handlerOut
                        self.btnCopyCodeModalBtnFadingLast = 2;
                        if ( !self.btnCopyCodeModalBtnFading ) {
                            self.btnCopyCodeModalBtnFadingLast = undefined;
                            self.btnCopyCodeModalBtnFading = true;
                            deps.$("#btnCopyCode").fadeOut().promise().done(function(){
                                self.btnCopyCodeModalBtnFading = false;
                                if ( self.btnCopyCodeModalBtnFadingLast == 1 )
                                    deps.$("#btnCopyCode").fadeIn();
                                else if (self.btnCopyCodeModalBtnFadingLast == 2 ) 
                                    deps.$("#btnCopyCode").fadeOut();
                            });
                        }
                            
                    });
                }

                var groupTBody = deps.$("#groupTBody");
                deps.$("#memberCount").text(self.groupData.players.length);
                groupTBody.html('');
                
                for ( let i = 0; i < self.groupData.players.length; i++) {
                    var player = self.groupData.players[i];
                    
                    var newTr = deps.$("<tr>"),
                    indexTd = deps.$("<td>").text(i+1),
                    nameTd = deps.$("<td>").text(player),
                    profileTd = deps.$("<td>").append(deps.$(`<a href="/profile/`+player+`" class="playerProfile">
                    `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("ProfileBtn"):"Profil")+`
                    </a>`));
                    
                    newTr.append(indexTd).append(nameTd).append(profileTd);
                    groupTBody.append(newTr);
                }
            }
            setupPlayersTablePlayer();
            deps.$("#btnInfoView").show();
            deps.$("#btnManageView").remove();
            deps.$("#btnRequestView").remove();
            deps.$("#btnHistoryView").removeClass("col-3").addClass("col-4");
            deps.$("#btnMessageView").removeClass("col-3").addClass("col-4");
            
            setupMessagesTable();

            setupHistoryTable();
        }
        
        var setupHistoryTable = () => {
            
            var groupTBody = deps.$("#groupTBody3");
            groupTBody.html('');
            if ( !self.groupHistory.content.length )
                groupTBody.html(`<div class="text-center noHistory"><div>`+
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noHistory"):"Brak historii")
                    +`</div></div>`);

            for ( let i = 0; i < self.groupHistory.content.length; i++) {
                var history = self.groupHistory.content[i];
                
                var newTr = deps.$(`<tr data-toggle="modal" data-target="#gameHistoryGroupModal" aria-hidden="true">`),
                indexTd = deps.$("<td>").text(i+1 + (self.groupHistory.pageable.pageSize * self.groupHistory.pageable.pageNumber)),
                descTd = deps.$("<td>").text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("GamePlayerInfo"):"Gra rozegrana")),
                dateTd = deps.$("<td>").text(history.date);

                newTr[0].dataset["id"] = history.id;
                newTr[0].dataset["date"] = history.date;

                newTr.on('click',(e)=> {
                    var target = $(e.target);
                    if ( !target.is("tr") )
                        target = target.closest("tr");
                    var id = target[0].dataset["id"];
                    var date = target[0].dataset["date"];
                    self.focusedHistoryId = id;
                    deps.$("#gameHistoryGroupBody").text(date);
                })

                newTr.append(indexTd).append(descTd).append(dateTd);
                groupTBody.append(newTr);
            }
        }

        var setupMessagesTable = () => {

            var groupTBody = deps.$("#groupTBody4");
            groupTBody.html('');

            var unreadMessages = 0;
            
            for ( let i = 0; i < self.groupMessages.length; i++) {
                var message = self.groupMessages[i];
                
                if ( !message.read )
                    unreadMessages++;
                var newTr = deps.$(`<tr data-toggle="modal" data-target="#displayMessageGroupModal" aria-hidden="true">`),
                indexTd = deps.$("<td>").text(i+1),
                nameTd = deps.$("<td>").text(message.username),
                titleTd = deps.$("<td>").text(message.title),
                creatTd = deps.$("<td>").text(message.creationDate),
                editTd = deps.$("<td>").text(message.editDate?message.editDate:""),
                readTd = deps.$("<td>").text((!message.read)?
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("UnReadInfo"):"Nowa wiadomość"):
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("ReadInfo"):"Przeczytana"));
                
                if ( !message.read ) {
                    readTd.addClass("bg-danger text-white")
                }

                newTr[0].dataset["id"] = message.id;

                newTr.on('click',(e)=> {
                    var target = $(e.target);
                    if ( !target.is("tr") )
                        target = target.closest("tr");
                    var id = target[0].dataset["id"];
                    self.focusedMessageId = id;
                    self.readMessage(id);
                })
                
                newTr.append(indexTd).append(nameTd).append(titleTd).append(creatTd).append(editTd).append(readTd);
                groupTBody.append(newTr);
            }

            deps.$("#messagesLobbyCountInfo").text(self.groupLobbies.length?self.groupLobbies.length:"");

            for ( let i = 0; i < self.groupLobbies.length; i++) {
                var lobbyCode = self.groupLobbies[i];

                var newTr = deps.$(`<tr data-toggle="modal" data-target="#displayMessageLobbyGroupModal" aria-hidden="true">`),
                indexTd = deps.$("<td>").text(i+1),
                nameTd = deps.$("<td>"),
                titleTd = deps.$("<td>").text("Lobby grupy"),
                creatTd = deps.$("<td>"),
                editTd = deps.$("<td>"),
                readTd = deps.$("<td>").text("Nowa wiadomość");
                
                readTd.addClass("bg-warning")

                newTr[0].dataset["lobby"] = lobbyCode;

                newTr.on('click',(e)=> {
                    var target = $(e.target);
                    if ( !target.is("tr") )
                        target = target.closest("tr");
                    var lobby = target[0].dataset["lobby"];
                    self.focusedMessagelobbyId = lobby;
                    self.readMessageLobby(lobby);
                })
                
                newTr.append(indexTd).append(nameTd).append(titleTd).append(creatTd).append(editTd).append(readTd);
                groupTBody.prepend(newTr);
            }
            if ( !self.groupMessages.length )
                groupTBody.html(`<div class="text-center noMessages"><div>`+
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noMessages"):"Brak komunikatów")
                    +`</div></div>`);
            else
               deps.$("#messagesCountInfo").text(unreadMessages?unreadMessages:"");
        }
        self.readMessage = (id) => {
            
            var message = self.groupMessages.filter(m=>{
                if ( m.id === id )
                    return true;
                else
                    return false;
            })[0];

            deps.$("#messageContentDisplay").html('');
            if ( message.content.split("[[href|").length == 2 && message.content.split("|]]").length == 2) {
                var span1 = deps.$("<span>").text(message.content.split("[[href|")[0]);
                var span2 = deps.$("<span>").text(message.content.split("|]]")[1]);
                var aHtml = deps.$("<a>").text("Dołącz przez link").attr("href","/game/" + 
                message.content.split("[[href|")[1].split("|]]")[0])

                deps.$("#messageContentDisplay").append(span1).append(aHtml).append(span2);
            } else {
                deps.$("#messageContentDisplay").text(message.content).show();
            }


            deps.$("#messageTitleDisplay").text(message.title).show();
            deps.$("#messagefromDisplay").text(message.username).show();
            deps.$("#messageOkBtn").show();

            
            deps.$("#inputMessageTitleEdit").val(message.title).parent().hide();
            deps.$("#inputMessageContentEdit").val(message.content).parent().hide();
            deps.$("#cancelEditedBtn").hide();
            deps.$("#saveEditedBtn").hide();

            if ( self.isLecturer)
                deps.$("#editMessageBtn").show();
            
            Ajax.postReadMessage(id,self.afterReadMessage);
        }

        self.readMessageLobby = (lobby) => {
            
            deps.$("#messageLobbyContentDisplay").html('');

            var span1 = deps.$("<span>").text("dołącz przez kod: " + lobby + ", \n lub link: ");
            var aHtml = deps.$("<a>").text(lobby).attr("href","/game/" + lobby)

            deps.$("#messageLobbyContentDisplay").append(span1).append(aHtml);
        }

        self.afterReadMessage = (data) => {
            
            Ajax.getMessagesFor(self.groupData.groupCode, (data) => {
                self.groupMessages = data;

                if ( self.isLecturer)
                    setupLecturerPage();
                else
                    setupPlayerPage();
            })
        }

        self.afterDeleteUserFromGroup = (data) => {

            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgDeletedUserError"):"Nie usunięto użytkownika z grupy"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgDeletedUserSuccess"):"Użytkownik usunięty"), 
                true);

            Ajax.getGroupInfo(self.groupData.groupCode, (data)=> {
                self.groupData = data;
                setupLecturerPage();
            });
        }
        
        self.afterAcceptRequest = (data) => {

            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgUserAcceptedError"):"Nie udało się zaakceptować użytkownika do grupy"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgUserAcceptedSuccess"):"Zaakceptowano użytkownika do grupy"), 
                true);

            Ajax.getGroupRequests(self.groupData.groupCode, (data)=> {
                self.groupRequests = data;
                
                deps.$("#requestsCountInfo").text(self.groupRequests.length?self.groupRequests.length:"");
                setupLecturerPage();
            });
        }

        self.afterDeclineRequest = (data) => {

            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgUserRequestRemovedError"):"Nie usunięto prośby użytkownika"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgUserRequestRemovedSuccess"):"Usunięto prośbę użytkownika"), 
                true);

            Ajax.getGroupRequests(self.groupData.groupCode, (data)=> {
                self.groupRequests = data;
                
                deps.$("#requestsCountInfo").text(self.groupRequests.length?self.groupRequests.length:"");
                setupLecturerPage();
            });
        }



        self.updadeValidationInfoTorGroupInput = (groupName) => {

            deps.$("#invalidGroupInfoExists").hide();
            deps.$("#invalidGroupInfoBad").hide();
            deps.$("#newGroupInput").css("color","initial");
            var isValid = true;
            if (self.groupNames.includes(groupName)) {
                isValid = false;
                deps.$("#invalidGroupInfoExists").show();
                deps.$("#newGroupInput").css("color","red");
            } else if ( !validateGroupName(groupName) && groupName != ""){
                
                isValid = false;
                deps.$("#invalidGroupInfoBad").show();
                deps.$("#newGroupInput").css("color","red");
            } 
            if (isValid) 
                self.updateGroupName(groupName);
            else 
                self.updateGroupName(isValid);
        }

        self.updateGroupName = (groupName) => {
            self.currentNewGroupName =  groupName;

            if ( !groupName)
                deps.$("#btnChangeNameGroup")[0].disabled = true;
            else
                deps.$("#btnChangeNameGroup")[0].disabled = false;
        }

        self.afterGroupNameChange = (data) => {
            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupNameNotChangedError"):"Nie zmieniono nazwy grupy"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupNameNotChangedSuccess"):"Zmieniono nazwę grupy"), 
                true);

            Ajax.getGroupInfo(self.groupData.groupCode, (data) => {

                self.groupData = data;
                setupLecturerPage();
            });
        }

        self.validateAndSend = () => {

            var title = deps.$("#inputMessageTitle").val();
            var content = deps.$("#inputMessageContent").val();
            if ( title.trim() === "") {

                deps.$("#invalidMessageTitle").show();
                setTimeout(function(){deps.$("#invalidMessageTitle").fadeOut()},5000);
            } else if (content.trim() === "") {

                deps.$("#invalidMessageTitle").hide();
                deps.$("#invalidMessageContent").show();
                setTimeout(function(){deps.$("#invalidMessageContent").fadeOut()},5000);
            } else {
                
                deps.$("#invalidMessageTitle").hide();
                deps.$("#invalidMessageContent").hide();

                deps.$("#messageModal").modal('hide');
                deps.$("#btnMessageGroup")[0].disabled = true;
                setTimeout(function(){deps.$("#btnMessageGroup")[0].disabled = false;},500);
                Ajax.postSendMessage(title, content, self.groupData.groupCode, self.afterSendMessage);
            }
        }


        self.afterUpdateMessage = (data) => {
            
            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgMessageEditedError"):"Nie udało się edytowac komunikatu"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgMessageEditedSuccess"):"Pomyślnie edytowano komunikat"), 
                true);

            Ajax.getMessagesFor(self.groupData.groupCode, (data) => {
                self.groupMessages = data;
                self.readMessage(self.focusedMessageId);
                setupLecturerPage();
            })
        }

        self.getAllGroupNamesIfNull = () => {
            
            if( !self.groupNames )
                Ajax.getGroupNames((data)=> {

                    if ( data !== false)
                        self.groupNames = data;
                })
        }

        var validateGroupName = (name) => {
            var validation = name.match(groupNameRegex);
            if (validation)
                return true;
            return false;
        }

        self.afterCreatingGroupLobby = (data) => {

            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupLobbyCreatedError"):"Nie stworzono grupowego lobby"), 
                    false);
                return;
            }

            var lobbyCode = data;

            Ajax.postSendMessage(
                "Gra grupowa", 
                "Dla grupy " + self.groupData.name + ",\nkod lobby: " + lobbyCode + " link: [[href|"+lobbyCode+"|]],\nrozpoczeła się: " + new Date().toLocaleString(), 
                self.groupData.groupCode, 
                (data)=> {
                    if ( data === false) {
                        displayInfoAnimation(
                            ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupLobbyMessageSendError"):"Nie wysłano komunikatu o grupowym lobby"), 
                            false);
                        return;
                    }

                    deps.window.location.replace("/game/"  + lobbyCode);
                });
        }

        self.afterSendMessage = (data) => {
            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgMessageSentError"):"Nie wysłano komunikatu"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgMessageSentSuccess"):"Wysłano komunikat"), 
                true);

            deps.$("#inputMessageTitle").val("");
            deps.$("#inputMessageContent").val("");

            Ajax.getMessagesFor(self.groupData.groupCode, (data) => {
                self.groupMessages = data;
                setupLecturerPage();
            })
        }

        self.afterLeaveGroup = (data) => {
            
            if ( data === false ) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgLeaveGroupError"):"Nie udało się opuścić z grupy!"), 
                    false);

                return;
            }

            deps.window.location.replace("/groups/1");
        }

        self.setupHistoryPagi = () => {
            
            deps.window.location.hash = 'historyPage=' + (self.groupHistory.number+1);

            if ( self.groupHistory.first && self.groupHistory.last) {
                deps.$('.pagiBtnHolder').attr('style','display:none !important');
            } else {
                deps.$('.pagiBtnHolder').attr('style','');
                
                deps.$(".pageNum").text(self.groupHistory.number + 
                    ((self.groupHistory.totalPages>1)? "/" + self.groupHistory.totalPages : "")
                    )
                
                if ( !self.groupHistory.last ) {
                    deps.$("#nextPagebtn").removeClass('invisible');
                    deps.$("#nextPageNumbtn").text(self.groupHistory.number+2)
                } else 
                    deps.$("#nextPagebtn").addClass('invisible');

                if ( !self.groupHistory.first ) {
                    deps.$("#prevPagebtn").removeClass('invisible');
                    deps.$("#prevPageNumbtn").text(self.groupHistory.number)
                } else 
                    deps.$("#prevPagebtn").addClass('invisible');
            }
        }

        self.cancelHistoryPagi = () => {
            deps.window.history.pushState("", document.title, window.location.pathname);
            deps.$('.pagiBtnHolder').attr('style','display:none !important');
        }

        self.historyPagiNext = () => {
            Ajax.getGroupHistoryPage(self.groupData.groupCode, self.groupHistory.number+2, (data)=> {
                self.groupHistory = data;
                self.setupHistoryPagi();
                setupHistoryTable();
            })
        }

        self.historyPagiPrev = () => {
            Ajax.getGroupHistoryPage(self.groupData.groupCode, self.groupHistory.number, (data)=> {
                self.groupHistory = data;
                self.setupHistoryPagi();
                setupHistoryTable();
            })
        }


        /* Other: */
        var tooltipsUpdate = () => {
    
            if ( deps.$('[data-toggle="tooltip"]').tooltip !== null && deps.$('[data-toggle="tooltip"]').tooltip !== undefined)
                deps.$('[data-toggle="tooltip"]').tooltip({
                    trigger : 'hover'
                });
        }

        self.resizeWindow = () => {
            if ( self.currentTaskVariant && !self.currentTaskVariant.isTaskDone) {
                deps.$("html").height("100%");
                function isInt(n) {
                    return n % 1 === 0;
                }
                
                var h1 = deps.$(deps.window.document).height();
                if ( isInt (h1))
                    h1 -= 1;
                deps.$("html").height(h1);
                
            } else {
                deps.$("html").height("100%");
    
            }
        }
    
        var displayInfoAnimation = (text, success = true) => {
            var previousMessages = deps.$(".failSuccessInfo");
            previousMessages.each((b,t)=>{
                deps.$(t).css({marginTop: '+=50px'});
            });

            var failInfoDiv = deps.$(`<div class="failSuccessInfo alert alert-`+(success?"success":"danger")+`">` + text + `</div>`)
            deps.$("body").append(failInfoDiv)
        
            failInfoDiv.animate({
              top: "6%",
              opacity: 1
            }, 2000, function() {
              // Animation complete.
              setTimeout(function(){
                failInfoDiv.animate({
                  top: "9%",
                  opacity: 0
                }, 1000, function() {
                  // Second Animation complete.
                  failInfoDiv.remove();
                });
              },2000)
              
            });
        }
        
        self.copyTextToClipboard = (text) => {
      
            var textArea = window.document.createElement("textarea");
            textArea.value = text
            window.document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            
            var successful = false;
            try {
                successful = window.document.execCommand('copy');
                var msg = successful ? 'successful' : 'unsuccessful';
                if (self.debug)
                    console.log('Copying text command was ' + msg);

                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgCodeCopiedSuccess"):"Skopiowano kod"), 
                    true);
            } catch (err) {
                if (self.debug)
                    console.log('Oops, unable to copy');
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgCodeCopiedError"):"Nie udało się skopiować kodu"), 
                    false);
            }
        
            window.document.body.removeChild(textArea);
            return successful;
        }

        /*       event listeners          */
        var listenersSetup = () =>{
                    
            deps.window.onresize = self.resizeWindow;

            if ( deps.$("#btnManageView").length )
                deps.$("#btnManageView").on('click', e => {
                    if ( !self.manageViewActive)
                        self.switchToView("Manage");
                });

            if ( deps.$("#btnRequestView").length )
                deps.$("#btnRequestView").on('click', e => {
                    if ( !self.requestsViewActive)
                        self.switchToView("Requests");
                });

            if ( deps.$("#btnHistoryView").length )
                deps.$("#btnHistoryView").on('click', e => {
                    if ( !self.historyViewActive)
                        self.switchToView("History");
                });
            if ( deps.$("#btnMessageView").length )
                deps.$("#btnMessageView").on('click', e => {
                    if ( !self.messageViewActive)
                        self.switchToView("Message");
                });
            
            if ( deps.$("#btnInfoView").length )
                deps.$("#btnInfoView").on('click', e => {
                    if ( !self.manageViewActive)
                        self.switchToView("Info");
                });

            if ( deps.$("#btnRemoveUserGroup").length) {
                deps.$("#btnRemoveUserGroup").on('click',e=> {
                    Ajax.deleteUserFromGroup(self.groupData.groupCode, self.focusedManageDeleteUser, self.afterDeleteUserFromGroup)
                });
            }

            if ( deps.$("#btnRemoveRequestGroup").length) {
                deps.$("#btnRemoveRequestGroup").on('click', e=> {
                    Ajax.postJoinRequest(self.focusedHistoryRequest,false,self.afterDeclineRequest);
                });
            }
            if ( deps.$("#btnAddRequestGroup").length) {
                deps.$("#btnAddRequestGroup").on('click', e=> {
                    Ajax.postJoinRequest(self.focusedHistoryRequest,true,self.afterAcceptRequest);
                });
            }
            
            if ( deps.$("#btnChangeNameModal").length) {

                self.btnChangeNameModalBtnFading = false;
                self.btnChangeNameModalBtnFadingLast;
                deps.$("#btnChangeNameModal").on('click',(e)=> {
                    self.getAllGroupNamesIfNull();
                    deps.$("#newGroupInput").val(self.groupData.name);
                });

                deps.$("#btnChangeNameModal").closest("div.col-6").hover( 
                    (e)=>{
                    // handlerIn
                    self.btnChangeNameModalBtnFadingLast = 1;
                    if ( !self.btnChangeNameModalBtnFading ){
                        self.btnChangeNameModalBtnFadingLast = undefined;
                        self.btnChangeNameModalBtnFading = true;
                        deps.$("#btnChangeNameModal").fadeIn().promise().done(function(){
                            self.btnChangeNameModalBtnFading = false;

                            if ( self.btnChangeNameModalBtnFadingLast == 1 ){
                                deps.$("#btnChangeNameModal").fadeIn();
                            } else if (self.btnChangeNameModalBtnFadingLast == 2 ){
                                deps.$("#btnChangeNameModal").fadeOut();
                            }
                        });
                    }
                },  (e)=>{
                    // handlerOut
                    self.btnChangeNameModalBtnFadingLast = 2;
                    if ( !self.btnChangeNameModalBtnFading ) {
                        self.btnChangeNameModalBtnFadingLast = undefined;
                        self.btnChangeNameModalBtnFading = true;
                        deps.$("#btnChangeNameModal").fadeOut().promise().done(function(){
                            self.btnChangeNameModalBtnFading = false;
                            if ( self.btnChangeNameModalBtnFadingLast == 1 ) {
                                deps.$("#btnChangeNameModal").fadeIn();
                            } else if (self.btnChangeNameModalBtnFadingLast == 2 ){
                                deps.$("#btnChangeNameModal").fadeOut();
                            }
                        });
                    }
                        
                });
            }
            
            if ( deps.$("#btnRemoveGroup").length) {
                deps.$("#btnRemoveGroup").on('click', (e)=>{
                    Ajax.deleteGroup(self.groupData.groupCode,(data) => {
                        if ( data === false) {
                            displayInfoAnimation(
                                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupRemovedError"):"Nie usunięto grupy"), 
                                false);
                            return;
                        }
                        displayInfoAnimation(
                            ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgGroupRemovedSuccess"):"Usunięto grupę"), 
                            true);

                        deps.window.location.replace("/groups/1");
                    });
                });
            }

            if ( deps.$("#btnGroupLobby").length) {
                deps.$("#btnGroupLobby").on('click', (e) => {
                    Ajax.postCreateGroupLobby(self.groupData.groupCode,self.afterCreatingGroupLobby);
                });
            }

            self.counter = 0;
            if ( deps.$("#btnMessageGroup").length) {
                deps.$("#btnMessageGroup").on('click', (e) => {
                    self.validateAndSend();
                });
            }
            if ( deps.$("#btnLeaveGroup").length) {
                deps.$("#btnLeaveGroup").on('click', (e) => {
                    
                    Ajax.postLeaveGroup(self.groupData.groupCode, self.afterLeaveGroup);
                });
            }

            
            if ( deps.$("#btnChangeNameGroup").length) {
                deps.$("#btnChangeNameGroup").on('click',(e)=> {
                    Ajax.putGroupName(self.currentNewGroupName, self.groupData.groupCode, self.afterGroupNameChange);
                });
            }

            if (deps.$("#newGroupInput").length) {
                deps.$("#newGroupInput").on('change input click',(e)=> {
                    var input = deps.$(e.target);

                    self.updadeValidationInfoTorGroupInput(input.val().trim());
                });
            }
            
            deps.$("#newGroupInput").keydown(e=> {
                var code = e.keyCode || e.which;
                if ( code == 13 ) { 
                    deps.$("#btnChangeNameGroup").trigger("click");
                }
            })

            if (deps.$("#btnCopyCode").length) {
                deps.$("#btnCopyCode").on("click",(e) => {
                    self.copyTextToClipboard(self.groupData.groupCode);
                });

                self.btnCopyCodeModalBtnFading = false;
                self.btnCopyCodeModalBtnFadingLast;

                deps.$("#btnCopyCode").closest("div.col-6").hover( 
                    (e)=>{
                    // handlerIn
                    self.btnCopyCodeModalBtnFadingLast = 1;
                    if ( !self.btnCopyCodeModalBtnFading ){
                        self.btnCopyCodeModalBtnFadingLast = undefined;
                        self.btnCopyCodeModalBtnFading = true;
                        deps.$("#btnCopyCode").fadeIn().promise().done(function(){
                            self.btnCopyCodeModalBtnFading = false;

                            if ( self.btnCopyCodeModalBtnFadingLast == 1 )
                                deps.$("#btnCopyCode").fadeIn();
                            else if (self.btnCopyCodeModalBtnFadingLast == 2 )
                                deps.$("#btnCopyCode").fadeOut();
                        });
                    }
                },  (e)=>{
                    // handlerOut
                    self.btnCopyCodeModalBtnFadingLast = 2;
                    if ( !self.btnCopyCodeModalBtnFading ) {
                        self.btnCopyCodeModalBtnFadingLast = undefined;
                        self.btnCopyCodeModalBtnFading = true;
                        deps.$("#btnCopyCode").fadeOut().promise().done(function(){
                            self.btnCopyCodeModalBtnFading = false;
                            if ( self.btnCopyCodeModalBtnFadingLast == 1 )
                                deps.$("#btnCopyCode").fadeIn();
                            else if (self.btnCopyCodeModalBtnFadingLast == 2 ) 
                                deps.$("#btnCopyCode").fadeOut();
                        });
                    }
                        
                });
            }
            if ( deps.$("#editMessageBtn").length) {
                deps.$("#editMessageBtn").on('click',(e)=>{

                    deps.$("#messageTitleDisplay").hide();
                    deps.$("#messageContentDisplay").hide();
                    deps.$("#messagefromDisplay").hide();
                    deps.$("#messageOkBtn").hide();
                    deps.$("#editMessageBtn").hide();

                    deps.$("#inputMessageTitleEdit").parent().show();
                    deps.$("#inputMessageContentEdit").parent().show();
                    deps.$("#cancelEditedBtn").show();
                    deps.$("#saveEditedBtn").show();
                });
            }

            if ( deps.$("#cancelEditedBtn").length) {
                deps.$("#cancelEditedBtn").on('click', (e)=>{
                    self.readMessage(self.focusedMessageId);
                });
            }


            if ( deps.$("#saveEditedBtn").length) {
                deps.$("#saveEditedBtn").on('click',(e)=>{

                    var title = deps.$("#inputMessageTitleEdit").val();
                    var content = deps.$("#inputMessageContentEdit").val();
                    Ajax.putUpdateMessage(title, content, self.focusedMessageId, self.afterUpdateMessage);
                });
            }

            if ( deps.$("#btnGameHistoryGroup").length ) {
                deps.$("#btnGameHistoryGroup").on('click', (e) => {
                    deps.window.location.replace("/game/results/" + self.focusedHistoryId);
                })
            }

            if ( deps.$("#nextPagebtn").length ) {
                deps.$("#nextPagebtn").on('click', (e) => {
                    self.historyPagiNext();
                })
            }
            if ( deps.$("#prevPagebtn").length ) {
                deps.$("#prevPagebtn").on('click', (e) => {
                    self.historyPagiPrev();
                })
            }
        }

        /*  initalization  */
        GroupLogicInit(data);
         
        return self;
    }
    
    GroupLogic.getInstance = (dataLazy, successfulCreationCallback) => {
    
        if (GroupLogic.singleton)
            return GroupLogic.singleton;

        var getCurrentURLGroupCode = () => {

            var urlGroupSplit = decodeURI(deps.window.location.href).split("group/");
            var urlGroup =  urlGroupSplit[urlGroupSplit.length-1];

            if ( deps.window.location.hash )
                urlGroup = urlGroup.replace(deps.window.location.hash, "");

            if ( urlGroup.length !== 9 )
                throw Error("Incorect URL, expected to have a group code!");
            else
                return urlGroup;
        }

        var getCurrentURLHistoryPage = () => {

            var hash = deps.window.location.hash;
            if ( !hash || !hash.includes("#historyPage=") || isNaN(parseInt(hash.replace("#historyPage=", ""))))
                return 1;
            else 
                return parseInt(hash.replace("#historyPage=", ""));
        }

        if ( dataLazy )
            return GroupLogic(dataLazy, successfulCreationCallback);
        else {
            return Promise.all([
                Ajax.getWhoAmI(),
                Ajax.getAccountInfo(),
                Ajax.getGroupInfo(getCurrentURLGroupCode()),
                Ajax.getGroupRequests(getCurrentURLGroupCode()),
                Ajax.getGroupHistoryPage(getCurrentURLGroupCode(), getCurrentURLHistoryPage()),
                Ajax.getMessagesFor(getCurrentURLGroupCode()),
                Ajax.getGroupLobbiesFrom(getCurrentURLGroupCode())
            ]).then((values)=>{

                if ( !values[0] || !values[1] || !values[2] || !values[3] || !values[4] || !values[5] ){
                    if ( successfulCreationCallback )
                        successfulCreationCallback(false);
                    return;
                }
                
                var data = {
                    account:{
                        playerInfo: values[0],
                        accountInfo: values[1]
                    },
                    groupData: values[2],
                    groupCode: getCurrentURLGroupCode(),
                    groupRequests: values[3],
                    groupHistory : values[4],
                    groupHistoryPage : getCurrentURLHistoryPage(),
                    groupMessages: values[5],
                    groupLobbies: values[6]
                }

                GroupLogic.singleton = GroupLogic(data, successfulCreationCallback);

                return GroupLogic.singleton;
            }).catch((e) => {
                console.warn("failed to init");
                console.warn(e);
                if ( successfulCreationCallback )
                    successfulCreationCallback(false);
                // all requests finished but one or more failed
            });
        }
    }
    
    return {
        GroupLogic: GroupLogic,
        getInstance: GroupLogic.getInstance
    }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {GroupModule};