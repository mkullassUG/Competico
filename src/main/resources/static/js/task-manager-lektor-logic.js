/*
ma miec inne endpointy ajax od TaskCreatorLecturer

/tasks/import/global/ -> /lecturer/taskmanager/


/api/v1/tasks/ -> /api/v1/tasksets


*/
const TaskCreatorLecturerModule = (function(deps={}) {

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

    if ( typeof TaskCreatorCore === 'undefined' && typeof deps.TaskCreatorCore === 'undefined') 
        throw Error("TaskCreatorCore not defined");
    if ( typeof deps.TaskCreatorCore === "undefined" && typeof TaskCreatorCore != "undefined")
        deps.TaskCreatorCore = TaskCreatorCore;

    if ( typeof NavbarLogic === 'undefined' && typeof deps.NavbarLogic === 'undefined') 
        throw Error("NavbarLogic not defined");
    if ( typeof deps.NavbarLogic === "undefined" && typeof NavbarLogic != "undefined")
        deps.NavbarLogic = NavbarLogic;

    if ( typeof PageLanguageChanger === 'undefined' && typeof deps.PageLanguageChanger === 'undefined') 
        throw Error("PageLanguageChanger not defined");
    if ( typeof deps.PageLanguageChanger === "undefined" && typeof PageLanguageChanger != "undefined")
        deps.PageLanguageChanger = PageLanguageChanger;

    if ( typeof TaskGameCore === 'undefined' && typeof deps.TaskGameCore === 'undefined') 
        throw Error("TaskGameCore not defined");
    if ( typeof deps.TaskGameCore === "undefined" && typeof TaskGameCore != "undefined")
        deps.TaskGameCore = TaskGameCore;

    if ( typeof DualListModule === 'undefined' && typeof deps.DualListModule === 'undefined') 
        throw Error("DualListModule not defined");
    if ( typeof deps.DualListModule === "undefined" && typeof DualListModule != "undefined")
        deps.DualListModule = DualListModule;

    var Ajax = function(){
        var self = {};

        self.useFormToSendTasksetFile = (form, tasksetName, callback) => {

            var form_data = new FormData(form);
            if ( tasksetName )
                form_data.append('json', tasksetName);

            deps.$.ajax({
                url: deps.$(form).attr("action"),
                type: deps.$(form).attr("method"),
                contentType: 'json',
                data: form_data,
                processData: false,
                contentType: false,
                success: function (data, status)
                {
                    if (debug)
                        console.log(data);
                    if ( data !== "")
                        callback(0);
                    else if ( callback )
                        callback(data);
                },
                error: function (xhr, desc, err)
                {
                    if (debug)
                        console.log(xhr);
                    if ( callback )
                        callback(false);
                }
            }); 
        }

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

        /*  New Ajax  */

        self.deleteTaskset = (tasksetName, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/taskset",
                contentType: "application/json",
                data: JSON.stringify({
                    tasksetName: tasksetName,
                }),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("deleteTaskset success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("deleteTaskset error");
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.deleteTask = (taskID, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/task/" + taskID,
                contentType: "application/json",
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("sendAjaxDeleteTask success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data, taskID);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("sendAjaxDeleteTask error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.putTasksetName = (tasksetName, newTasksetName, callback) => { 

            return deps.$.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/taskset",
                contentType: "application/json",
                cache: false,
                data: JSON.stringify({
                    tasksetName: tasksetName,
                    newTasksetName: newTasksetName
                }),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("putTasksetName success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("putTasksetName error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.putTaskToTaskset = (tasksetName, taskID, callback) => { 

            deps.$.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/task/" + taskID + "/move",
                contentType: "application/json",
                cache: false,
                data: JSON.stringify({
                    tasksetName: tasksetName,
                }),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("putTaskToTaskset success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("putTaskToTaskset error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getTasksetsInfo = (isJson, callback ) => {
    
            return deps.$.ajax({
                type: "GET",
                cache: false,
                url: "/api/v1/tasksets/info",
                contentType: "application/json",
                success: function (data, status)
                {   
                    if (debug) {
                        console.log("getTasksetsInfo success");
                        console.log(data);
                    }
                    if (  isJson && !isJson(data) && callback)
                        callback(0);
                    else if ( callback )
                        callback(data);
                },
                error: function (xhr, desc, err)
                {
                    if (debug ) {
                        console.log("getTasksetsInfo error");
                        console.log(xhr);
                    }
                    if ( callback)
                        callback(false);
                }
            }); 
        }

        self.getImportedTasksFile = (isJson, taskSetName = 'default', callback) => { 
            
            return fetch("/api/v1/tasksets/json/file?tasksetName="+encodeURIComponent(taskSetName))
            .then(resp => resp.blob())
            .then(blob => Promise.all([blob.text(),blob]))
            .then( values => {
                if ( !isJson(values[0]) && callback) {
                    callback(0);
                } else if ( callback )
                    callback(values[1]);
            })
            .catch((e) => {
                console.warn(e);
                if ( callback )
                    callback(false);
            });

        }

        self.getAllTasksetsFile = (isJson, callback) => {
            
            return fetch("/api/v1/tasksets/all/json/file")
            .then(resp => resp.blob())
            .then(blob => Promise.all([blob.text(),blob]))
            .then( values => {
                if ( !isJson(values[0]) && callback)
                    callback(0);
                else if ( callback )
                    callback(values[1]);
            })
            .catch((e) => {
                console.warn(e);
                if ( callback )
                    callback(false);
            });
        }

        self.getNumberOfTasks = (taskSetName, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/tasks/imported/count",
                contentType: "application/json",
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("getNumberOfTasks success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data)
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("getNumberOfTasks error");
                    }
                    if ( callback )
                        callback(false)
                }
            });
        }

        self.sendTask = (task, tasksets, callback, isJson) => {

            return deps.$.ajax({
                type: "POST",
                cache: false,
                data: JSON.stringify({
                    tasksetName: tasksets,
                    tasksetContent: task
                }),
                url: "/api/v1/tasksets/task",
                contentType: "application/json",
                success: function (data, status)
                {
                    if ( debug ) {
                        console.log("sendAjaxTask sucess");
                        console.log(data);
                    }
                    if ( isJson && !isJson(data) && callback )
                        callback(0);
                    else if ( callback )
                        callback(data);
                },
                error: function (xhr, desc, err)
                {   
                    if ( debug ) {
                        console.log("sendAjaxTask error");
                        console.log(xhr);
                    }
                    if ( callback )
                        callback(false);
                }
            }); 
        }

        self.createTaskset = (tasksetName, callback) => {

            var send = {tasksetName: tasksetName}

            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/taskset",
                contentType: "application/json",
                data     : JSON.stringify(send),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("createTaskset success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data)
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("createTaskset error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false)
                }
            });
        }

        self.getImportedTaskByID = (taskID, callback) => { 

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/task/" + taskID,
                contentType: "application/json",
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("getImportedTaskByID success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.log("getImportedTaskByID error");
                        console.log(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.putEditTask = (task, taskID, callback) => {

            return deps.$.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/task/" + taskID,
                contentType: "application/json",
                data     : JSON.stringify(task),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("putEditTask success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data, taskID)
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("putEditTask error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false)
                }
            });
        }

        self.deleteAllTasks = (taskset, callback) => { 

            var send = {tasksetName: taskset};	
            deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/tasksets/tasks",
                contentType: "application/json",
                data     : JSON.stringify(send),
                success: function(data, textStatus, jqXHR) {
                    if (debug) {
                        console.log("deleteAllTasks success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(true);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("deleteAllTasks error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }  

        return self;
    }();

    const TaskCreatorLecturerLogic = (data, successfulCreationCallback) => {

        /*  singleton   */
        if (TaskCreatorLecturerLogic.singleton)
            return TaskCreatorLecturerLogic.singleton;
        var self = {};
        if (!TaskCreatorLecturerLogic.singleton && data)
            TaskCreatorLecturerLogic.singleton = self;
        else if (!TaskCreatorLecturerLogic.singleton && !data) 
            return TaskCreatorLecturerLogic.getInstance(null, successfulCreationCallback);
    
        /*       logic variables          */
        self.playerInfo = data.account;
        debug = debug;
        self.focusedTaskID; 
        self.lastEditedTaskID; 
        self.currentTaskVariant;
        self.currentVariant; 
        self.CreatorCore;
        self.tablicaPolskichNazwTaskow;
        self.allTaskIds = [];
        self.oldTaskIds = [];
        self.newTaskIds = [];
        self.editedTaskIds = [];
        self.recentlyAddedTaskId;
        self.currentTaskNameString;
        self.isFirstTableSetup = true;
        
        self.tasksets = data.tasksets;
        self.tasksetOfCurrentTask; 
        self.currentlyEditingTaskIds = [];
        self.currentEditingTasksetNames = [];
        var saveTaskToChosenTaskset = false;
        const dualListLogic = DualListModule(deps).DualListLogic({selector:"#dualList", tasksets: Object.keys(data.tasksets)});
        var tasksetRegex = /^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:\"'`!@#$%^&*\(\)\[\]\{\}_+=|\\-]{2,32}$/;

        /*       logic functions          */
        var TaskCreatorLecturerLogicInit = (playerInfo) => {
            
            deps.PageLanguageChanger(false, debug, deps, self.InitWithPageLanguageChanger);

            var roles = self.playerInfo.accountInfo.roles?self.playerInfo.accountInfo.roles:[];
            if ( !roles.includes("TASK_DATA_ADMIN")) {
                $("#btnTaskConverter").remove();
                $("#convertTasksModalCenter").remove();
            } else {
                $("#btnTaskConverter").removeClass('collapse');

            }

            self.CreatorCore = deps.TaskCreatorCore(false, debug, deps);
            self.tablicaPolskichNazwTaskow = self.CreatorCore.GameCore.tablicaPolskichNazwTaskow;
            
            var tasksetsKeys = Object.keys(self.tasksets);
            for ( let i = 0; i < tasksetsKeys.length; i++) {
                var tasksetName = tasksetsKeys[i];
                var taskset = self.tasksets[tasksetName];
                for ( let j = 0; j < taskset.length; j++) {
                    var task = taskset[j];
                    self.allTaskIds.push(task.taskID)
                    self.oldTaskIds.push(task.taskID)
                }
            }

            if (URLHasTaskVariantName())
                self.changeVariant(getTaskVariantNameIfExists());
            if (URLHasTasksetName()) 
                self.openTaskset(getTasksetNameIfExists());
            else
                self.openTaskset('default');

            self.setupTitleAndButtonsForVariantsPresence();
                
            otherFrontendLogic();
    
            deps.$('#saveTaskModalCenter').on('shown.bs.modal', function (e) {
                deps.$('#btnSaveTask').one('focus', function (e) {
                    deps.$(this).blur();
                });
            });
            deps.$('#saveEditTaskModalCenter').on('shown.bs.modal', function (e) {
                deps.$('#btnSaveEditedTask').one('focus', function (e) {
                    deps.$(this).blur();
                });
            });
            deps.$('#editTaskModalCenter').on('shown.bs.modal', function (e) {
                deps.$('[data-taskid="'+self.focusedTaskID+'"]').one('focus', function (e) {
                    deps.$(this).blur();
                });
            });
            deps.$('#deleteTaskModalCenter').on('shown.bs.modal', function (e) {
                deps.$('[data-taskid="'+self.focusedTaskID+'"]').one('focus', function (e) {
                    deps.$(this).blur();
                });
            });
            
            Object.keys(self.tablicaPolskichNazwTaskow).map(key=>{
    
                deps.$("#"+key+"Div .taskEditTitle").html(self.tablicaPolskichNazwTaskow[key] + ( debug? " ("+key+")":""));
            })
            
            tooltipsUpdate();  
    
            if ( debug ) {
                deps.$("#main_nav").find(".disabled").addClass("bg-danger").removeClass("disabled");
            }
    
            
            var setupSlider = (id) => {
                
                const slider = deps.$("#" +id)[0];
                const min = slider.min;
                const max = slider.max;
                const value = slider.value;
    
                slider.style.background = `linear-gradient(to right, #007bff 0%, #007bff ${(value-min)/(max-min)*100}%, #DEE2E6 ${(value-min)/(max-min)*100}%, #DEE2E6 100%)`;
    
                function sliderChange() {
                    this.style.background = `linear-gradient(to right, #007bff 0%, #007bff ${(this.value-this.min)/(this.max-this.min)*100}%, #DEE2E6 ${(this.value-this.min)/(this.max-this.min)*100}%, #DEE2E6 100%)`;
                };
    
                deps.$(slider).on("input change", sliderChange);
            }
    
            for ( key in self.tablicaPolskichNazwTaskow) {
                setupSlider("customRange" + key);
            }
    
            deps.NavbarLogic(self.playerInfo.accountInfo, debug);
    
            listenersSetup();
            
            if ( typeof MessagesModule !== undefined )
                MessagesModule(deps).getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});
                
            if (successfulCreationCallback)
                successfulCreationCallback(true);
        }
        
        self.InitWithPageLanguageChanger = (data, lang) => {

            if ( data === true){
                return;
            }

            if (deps.$("#currentTasksetTaskCountSidepanelHeader").length)
                deps.$("#currentTasksetTaskCountSidepanelHeader").text(deps.$("#currentTasksetTaskCountSidepanelHeader").text().replace(/(^(Ilość zadań: )|^(Task number: ))/, ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("taskCount",lang,true):"Ilość zadań: ")));
            if (deps.$("#openNavButton").length)
                deps.$("#openNavButton").text(deps.$("#openNavButton").text().replace(/(^(☰ Tasksets)|^(☰ Zestawy))/, ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("tasksets",lang,true):"☰ Zestawy")));

            if (deps.$("#infoNavBtn").length)
                deps.$("#infoNavBtn").text(deps.$("#infoNavBtn").text().replace(/(^(Tasksets)|^(Zestawy))/, ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("tasksets2",lang,true):"Zestawy")));
        }

        /* messages */
        self.messagerFunction = (data) => {

            if ( data.areNew ) {
                //Opcja dodania efektu dźwiękowego:
                //console.log("Ding!")
            }
    
            var updateNavbar = () => {
                $("#messageUnreadMessages").text(
                    (data.numberOfMessages+data.numberOfLobbies)?
                    ((data.numberOfMessages+data.numberOfLobbies)>99?
                    "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
                $("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
            }
            updateNavbar();
        }
        /*       event listeners          */
        var listenersSetup = () =>{
            
            var prepareWariantButtonListeners = (tablicaPolskichNazwTaskow) => {
    
                var makeBtnListener = (wariantName) => {
                    if (deps.$("#btn" + wariantName).length)
                        deps.$("#btn" + wariantName).on("click",() => {
                            var wariantNameInner = wariantName;
                            self.changeVariant(wariantNameInner);
                            
                            deps.$("#mainNavbarDropdown").hasClass("show")?deps.$(`[data-target="#mainNavbarDropdown"]`).click() :null;
                        });
        
                    if (deps.$("."+ wariantName +"BtnClass").length)
                        deps.$("."+ wariantName +"BtnClass").on("click",() => {
                            var wariantNameInner = wariantName;
                            self.changeVariant(wariantNameInner);
        
                        });
                }
                for ( wariantName in tablicaPolskichNazwTaskow)
                    makeBtnListener(wariantName);
            }
    
            prepareWariantButtonListeners(self.tablicaPolskichNazwTaskow);
            
            if ( deps.$("#taskSettingsPanel, #btnStopCreatingTask").length) {
                if ( deps.$("#btnStopCreatingTask").length) {
                    deps.$("#btnStopCreatingTask").on('click', (e) => {
                        $("#taskSettingsPanel")[0].click();
                    })
                }

                deps.$("#taskSettingsPanel").on('click', (e) => {
                    var tasksetSettingsButtonHref = $("#taskSettingsPanel").attr("href").split("#");
                    
                    if ( tasksetSettingsButtonHref[1] && tasksetSettingsButtonHref !== deps.window.location.hash.replace("#",""))
                        deps.window.location.hash = tasksetSettingsButtonHref[1];

                    self.setupTitleAndButtonsForVariantsPresence();
                });
            }


            if (deps.$("#btnDownloadJsonImportedTasksSet, #btnDownloadJsonImportedTasks").length)
                deps.$("#btnDownloadJsonImportedTasksSet, #btnDownloadJsonImportedTasks").on("click",()=>{
                    self.downloadImportedTasks();
                });

            if (deps.$("#btnSendSaveTask").length)
                deps.$("#btnSendSaveTask").on("click",()=>{
                    
                    var tasksets = getChosenTasksets();
                    if ( !tasksets.length )
                        displayInfoAnimation("Nie znaleziono poprawnej nazwy zestawu.", false);
                    else
                        self.sendTask(tasksets);
                });
            if (deps.$("#btnSendEditTask").length)
                deps.$("#btnSendEditTask").on("click",(e)=>{
                    self.editTask([self.focusedTaskID], true);
                });
            if (deps.$("#btnSendSaveEditTask").length)
                deps.$("#btnSendSaveEditTask").on("click",(e)=>{
                    self.saveEditTask();
                });
            if (deps.$("#btnSendDeleteTask").length)
                deps.$("#btnSendDeleteTask").on("click",(e)=>{
                    self.deleteTask(self.focusedTaskID);
                });
            if (deps.$("#btnSendDeleteAllTasks").length)
                deps.$("#btnSendDeleteAllTasks").on("click",(e)=>{
                        
                    Ajax.deleteAllTasks(self.currentEditingTasksetNames[0],
                        (data => {
                            if ( data === false ) {
                                displayInfoAnimation("Nie usunięto.", false);
                                return;
                            }

                            var mapOfAllTaskIdFromTaskset = self.tasksets[self.currentEditingTasksetNames[0]].map(task => task.taskID);

                            self.allTaskIds = self.allTaskIds.filter(t => !mapOfAllTaskIdFromTaskset.includes(t));
                            self.oldTaskIds = self.oldTaskIds.filter(t => !mapOfAllTaskIdFromTaskset.includes(t));
                            self.newTaskIds = self.newTaskIds.filter(t => !mapOfAllTaskIdFromTaskset.includes(t));
                            self.editedTaskIds = self.editedTaskIds.filter(t => !mapOfAllTaskIdFromTaskset.includes(t));
                            
                            if ( mapOfAllTaskIdFromTaskset.includes(self.focusedTaskID))
                                self.focusedTaskID = undefined;
                            if ( mapOfAllTaskIdFromTaskset.includes(self.lastEditedTaskID))   
                                self.lastEditedTaskID = undefined;
                            if(deps.$("#btnSaveEditedTask").length > 0)
                                deps.$("#btnSaveEditedTask").hide();
            
                            displayInfoAnimation("Pomyślnie usunięto.", true);
                            self.deleteAllTasksFromTableVisually();
                        }));
                });
            if (deps.$("#btnDemoTask").length)
                deps.$("#btnDemoTask").on("click",(e)=>{
                    self.setupDemo();
                });
            if (deps.$("#btnDemoTaskEnd").length)
                deps.$("#btnDemoTaskEnd").on("click", (e) => {
                    self.endDemo();
                });
                
            if ( deps.$("#importAllTasksetsToFileBtn").length) {
                deps.$("#importAllTasksetsToFileBtn").on('click',(e)=>{
                    Ajax.getAllTasksetsFile(
                        isJson,
                        (blob) => {

                        if ( blob === false) {
                            displayInfoAnimation("Nie pobrano pliku.", false);
                            return;
                        } else if ( blob === 0){
                            displayInfoAnimation("Nie pobrano pliku, (Czy jesteś zalogowany jako lektor?)", false);
                            return;
                        }
    
                        const url = deps.window.URL.createObjectURL(blob);
                        const a = $('<a>');
                        a[0].style.display = 'none';
                        a[0].href = url;
                        a[0].download = 'importedTasksetsFile.json';
                        deps.window.document.body.appendChild(a[0]);
                        a[0].click();
                        deps.window.URL.revokeObjectURL(url);
                        displayInfoAnimation("Pomyślnie pobrano plik.", true);
                    });
                })
            }

            if (deps.$("#FileInputSingleTaskset").length) {

                var singleTasksetName = "FileInputSingleTaskset";
                deps.$("#" + singleTasksetName).change( (evt)=>{fileInputTableChange(singleTasksetName,evt)});
                if (deps.$("#btnUploadJsonImportedTasks, #btnUploadJsonImportedTasksSet").length)
                    deps.$("#btnUploadJsonImportedTasks, #btnUploadJsonImportedTasksSet").on('click', () => {

                        fileInputRefreshAllVisually(singleTasksetName);
                        var input = deps.$("#"+singleTasksetName)[0];
                        input.value = '';
                        var nextSibling = input.nextElementSibling
                        nextSibling.innerText = ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("chooseFile"):"Wybierz plik");
                    });
            }

            if (deps.$("#FileInputMultipleTasksets").length) {

                var multipleTasksetsName = "FileInputMultipleTasksets";
                deps.$("#"+multipleTasksetsName).change( (evt)=>{fileInputTableChange(multipleTasksetsName,evt)});
                if (deps.$("#btnUploadJsonImportedTasksToTaskset").length)
                    deps.$("#btnUploadJsonImportedTasksToTaskset").on('click', () => {

                        fileInputRefreshAllVisually(multipleTasksetsName);
                        var input = deps.$("#"+multipleTasksetsName)[0];
                        input.value = '';
                        var nextSibling = input.nextElementSibling
                        nextSibling.innerText = ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("chooseFile"):"Wybierz plik");
                    });
            }
            
            if (deps.$("#customConvertFile").length) {

                var refreshAllVisuallyConvert = () => {
                    deps.$("#convertFileInvalidBadFile").hide();
                    deps.$("#convertFileInvalidEmptyFile").hide();
                    deps.$("#convertedTaskTableTBody").html("");
                    deps.$("#convertedTaskTable").addClass('collapse');
                    deps.$("#btnSendConvertTasks").addClass('disabled').prop("disabled", true);
                    deps.$("#customConvertFileLabel").removeClass("invalid-file").removeClass("valid-file");
                }

                deps.$("#customConvertFile").change( (evt) => {
    
                    var input = deps.$("#customConvertFile")[0];
                    if ( !input.files[0] ||  !input.files[0].name)
                        return;
    
                    var fileName = input.files[0].name;
    
                    var nextSibling = input.nextElementSibling
                    nextSibling.innerText = fileName
    
                    function readFile (evt) {	
                            
                        var Input = evt.target;
                        var fileTypes = ['json'];  //acceptable file types
    
                        return new Promise((resolve) => {
                        
                            if (Input.files && Input.files[0]) {
                                var extension = Input.files[0].name.split('.').pop().toLowerCase(),  //file extension from Input file
                                isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types
                                if(isSuccess) {
                                    var reader = new FileReader();
                                    reader.onload = function (e) {
                                        var str = e.target.result;
                                        if (isJson(str)) {
                                            resolve( str );
                                        } else {
                                            resolve( false );
                                        }
                                    }
                                    reader.readAsText(Input.files[0]);
                                } else {
                                    resolve( false );
                                }
                                
                            }
                        });
                    }
    
                    var showTaskArrayImportTable = (jsonStr) => {
    
                        for ( let i = 0; i < jsonStr.length; i++) {
                            var task = jsonStr[i];
                            deps.$("#convertedTaskTableTBody").append(`
                            <tr>
                                <td class="w-25">
                                    `+(i+1)+`
                                </td>
                                <td>
                                    `+task.taskName+`
                                </td>
                                <td class="bg-warning">
                                    -
                                </td>
                            </tr>`)
                        }
                    }
                    
                    var showTasksetImportTable = (jsonStr, taskCount = 0) => {

                        var tasksetName = jsonStr.tasksetName;
                        var tasksetContent = jsonStr.tasksetContent;
                        if ( tasksetContent.length)
                            for ( let i = 0; i < tasksetContent.length; i++) {
                                var task = tasksetContent[i];
                                deps.$("#convertedTaskTableTBody").append(`
                                <tr>
                                    <td class="w-25">
                                        `+(taskCount+1)+`
                                    </td>
                                    <td>
                                        `+task.taskName+`
                                    </td>
                                    <td>
                                        `+tasksetName+`
                                    </td>
                                </tr>`);
                                taskCount++;
                            }
                        else 
                            deps.$("#convertedTaskTableTBody").append(`
                            <tr>
                                <td class="w-25">
                                    `+(taskCount+1)+`
                                </td>
                                <td>
                                    Empty
                                </td>
                                <td>
                                    `+tasksetName+`
                                </td>
                            </tr>`);
                        return taskCount;
                    }

                    var showTasksetArrayImportTable = (jsonStr) => {
                        var taskCount = 0;
                        for ( let i = 0; i < jsonStr.length; i++) {
                            var taskset = jsonStr[i];
                            taskCount = showTasksetImportTable(taskset, taskCount);
                        }
                    }
    
                    readFile(evt)
                    .then(jsonStr => {
                        
                        refreshAllVisuallyConvert();

                        if (jsonStr) {
    
                            jsonStr = JSON.parse(jsonStr);
                            
                            var typeOfData = checkIfJsonHasObjectWithTasksetOrArrayOfTasksOrArrayOfTasksets(jsonStr);
                            if ( typeOfData === 'other') {
                                deps.$("#convertFileInvalidEmptyFile").show();
                                deps.$("#customConvertFileLabel").addClass("invalid-file");
                                return;
                            }
    
                            deps.$("#btnSendConvertTasks").removeClass('disabled');
                            deps.$("#btnSendConvertTasks").prop("disabled", false);
                            deps.$("#convertedTaskTable").removeClass('collapse');
                            deps.$("#customConvertFileLabel").addClass("valid-file");
                            
                            if ( typeOfData === "array z zadankami")
                                showTaskArrayImportTable(jsonStr);
                            else  if ( typeOfData === 'jeden taskset')
                                showTasksetImportTable(jsonStr);
                            else if ( typeOfData === 'array z tasksetami')
                                showTasksetArrayImportTable(jsonStr);
                            else {
                                
                                if ( debug )
                                    console.warn("To nie powinno się wydarzyć! pewnie nie pusty plik json bez danych");
                            }

                        } else {
                            deps.$("#convertFileInvalidBadFile").show();
                            deps.$("#customConvertFileLabel").addClass("invalid-file");
                        }
                        
                    });
                });
    
                if (deps.$("#btnTaskConverter").length) {
                    deps.$("#btnTaskConverter").on('click', () => {
                        
                        refreshAllVisuallyConvert();
    
                        var input = deps.$("#customConvertFile")[0];
                        input.value = '';
                        var nextSibling = input.nextElementSibling
                        nextSibling.innerText = ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("chooseFile"):"Wybierz plik");
                    })
                }
            }
            
            if (deps.$("#btnSendFileInputSingleTaskset").length) {
    
                deps.$("#btnSendFileInputSingleTaskset").on('click',(e)=>{
    
                    deps.$("#FileInputSingleTasksetForm").submit();
                })
    
                deps.$('#FileInputSingleTasksetForm').submit(function(e) {
    
                    e.preventDefault();
                    
                    Ajax.useFormToSendTasksetFile(this, self.currentEditingTasksetNames[0], (data)=> {

                        if ( data === false) {
                            displayInfoAnimation("Nie udało się importowac zadań do zestawu", false);
                            return;
                        } if ( data === 0) {
                            displayInfoAnimation("Nie udało się importowac zadań do zestawu, (Czy jesteś zalogowany jako lektor?)", false);
                            return;
                        }
                        
                        self.updateTasksetsInfo();
                        displayInfoAnimation("Pomyślnie importowano zadania do zestawu.", true);
                    })
                    
                    
                }); 
            }

            if ( deps.$("#btnSendSaveTaskset").length ) {
                deps.$("#btnSendSaveTaskset").on('click',(e)=>{
                    self.createNewTaskset();
                })
            }

            if (deps.$("#btnSendFileInputMultipleTasksets").length) {
    
                deps.$("#btnSendFileInputMultipleTasksets").on('click',(e)=>{
    
                    deps.$("#FileInputMultipleTasksetsForm").submit();
                })
    
                deps.$('#FileInputMultipleTasksetsForm').submit(function(e) {
    
                    e.preventDefault();
                    
                    Ajax.useFormToSendTasksetFile(this, false, (data) => {
                        
                        if ( data === false ) {
                            displayInfoAnimation("Nie udało się importowac zadań.", false);
                            return;
                        } if ( data === 0) {
                            displayInfoAnimation("Nie udało się importowac zadań, (Czy jesteś zalogowany jako lektor?)", false);
                            return;
                        }

                        self.updateTasksetsInfo();
                        displayInfoAnimation("Pomyślnie importowano zadania.", true);
                    });
                }); 
            }
            
            if (deps.$("#btnSendConvertTasks").length) {
    
                deps.$("#btnSendConvertTasks").on('click',(e)=>{
    
                    deps.$("#TaskJsonFileFormConvert").submit();
                })
    
                deps.$('#TaskJsonFileFormConvert').submit(function(e) {
                    $('#convertTasksModalCenter').modal('hide');
                }); 
            }
            
            if (deps.$("#saveToTasksetCB").length) {
                deps.$("#saveToTasksetCB").on("change, click", (e)=> {
                    saveTaskToChosenTaskset = $("#saveToTasksetCB")[0].checked;
                    if (saveTaskToChosenTaskset)
                        showTasksetModalPart();
                    else
                        hideTasksetModalPart();

                    checkOnDisablingSaveTask();
                });
            }

            if (deps.$("#btnSaveTask").length) {
                deps.$("#btnSaveTask").on('click',(e) => {
                    prepareDualList();
                    deps.$("#newTasksetNameInput").val("");

                    checkOnDisablingSaveTask();
                })
            }

            if (deps.$("#newTasksetNameInput").length) {
                deps.$("#newTasksetNameInput").on('click input change focus blur',(e) => {
                    checkOnDisablingSaveTask();
                })
            }
            if (deps.$("#newTasksetNameInputSet").length) {
                deps.$("#newTasksetNameInputSet").on('click input change focus blur',(e) => {
                    checkOnDisablingSaveTaskInSet();
                })
            }
            if ( deps.$(".output-move-one, .output-move-multi, .input-move-one, .input-move-multi, #duallist-selected, #duallist-non-selected").length) {
                $(".output-move-one, .output-move-multi, .input-move-one, .input-move-multi, #duallist-selected, #duallist-non-selected").on('click change input', (e) => {
                    checkOnDisablingSaveTask();
                })
            }
            if (deps.$("#btnchangeTasksetNameModalCenter").length) {
                deps.$("#btnchangeTasksetNameModalCenter").on('click', (e) => {
                    self.editTasksetNameModalSetup();
                });
            }

            if (deps.$("#btnSendchangeTasksetName").length ) {
                deps.$("#btnSendchangeTasksetName").on('click', (e) => {
                    self.editTasksetNameSend();
                })
            }
            
            if (deps.$("#changeTasksetNameInput").length ) {
                deps.$("#changeTasksetNameInput").on('click input change focus blur', (e) => {
                    self.checkSetupButtonAndInformAboutTasksetName();
                })
            }

            if (deps.$("#tasksetTasksInfo").length ) {
                deps.$("#tasksetTasksInfo").on('click', (e) => {
                    self.prepareTasksetTasksInfo();
                })
            }
            
            if ( deps.$("#btnSendDeleteTaskset").length) {
                deps.$("#btnSendDeleteTaskset").on("click", (e) => {
                    self.deleteTaskset();
                })
            }

            deps.window.onresize = self.resizeWindow;
        }
        
        var fileInputTableChange = (name, evt) => {
            
            var input = deps.$("#" + name)[0];
            if ( !input.files[0] ||  !input.files[0].name)
                return;

            var fileName = input.files[0].name;

            var nextSibling = input.nextElementSibling
            nextSibling.innerText = fileName

            function readFile (evt) {	
                    
                var Input = evt.target;
                var fileTypes = ['json'];  //acceptable file types

                return new Promise((resolve) => {
                
                    if (Input.files && Input.files[0]) {
                        var extension = Input.files[0].name.split('.').pop().toLowerCase(),  //file extension from Input file
                        isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types
                        if(isSuccess) {
                            var reader = new FileReader();
                            reader.onload = function (e) {
                                var str = e.target.result;
                                if (isJson(str)) {
                                    resolve( str );
                                } else {
                                    resolve( false );
                                }
                            }
                            reader.readAsText(Input.files[0]);
                        } else {
                            resolve( false );
                        }
                        
                    }
                });
            }
            
            var showTasksetImportTable = (jsonStr, taskCount = 0) => {
                
                var tasksetName = jsonStr.tasksetName;
                var tasksetContent = jsonStr.tasksetContent;
                if ( tasksetContent.length)
                    for ( let i = 0; i < tasksetContent.length; i++) {
                        var task = tasksetContent[i];
                        deps.$("#"+name+"TableTBody").append(`
                        <tr>
                            <td class="w-25">
                                `+(taskCount+1)+`
                            </td>
                            <td>
                                `+task.taskName+`
                            </td>
                            <td>
                                `+tasksetName+`
                            </td>
                        </tr>`);
                        taskCount++;
                    }
                else 
                    deps.$("#"+name+"TableTBody").append(`
                    <tr>
                        <td class="w-25">
                            `+(taskCount+1)+`
                        </td>
                        <td>
                            Empty
                        </td>
                        <td>
                            `+tasksetName+`
                        </td>
                    </tr>`);
                return taskCount;
            }

            var showTasksetArrayImportTable = (jsonStr) => {
                
                var taskCount = 0;
                for ( let i = 0; i < jsonStr.length; i++) {
                    var taskset = jsonStr[i];
                    taskCount = showTasksetImportTable(taskset, taskCount);
                }
            }
            
            readFile(evt)
            .then(jsonStr => {
                
                fileInputRefreshAllVisually(name);

                if (jsonStr) {

                    jsonStr = JSON.parse(jsonStr);
                    
                    var typeOfData = checkIfJsonHasObjectWithTasksetOrArrayOfTasksOrArrayOfTasksets(jsonStr);
                    if ( typeOfData === 'other') {
                        
                        deps.$("#"+name+"InvalidEmptyFile").show();
                        deps.$("#"+name+"Label").addClass("invalid-file");
                        return;
                    } else if ( typeOfData === "array z zadankami"){
                        deps.$("#"+name+"InvalidOldFile").show();
                        deps.$("#"+name+"Label").addClass("invalid-file");
                        return;
                    }

                    deps.$("#btnSend" + name).removeClass('disabled').prop("disabled", false);
                    deps.$("#"+name+"Table").removeClass('collapse');
                    deps.$("#"+name+"Label").addClass("valid-file");
                    
                    
                    if ( typeOfData === 'jeden taskset')
                        showTasksetImportTable(jsonStr);
                    else if ( typeOfData === 'array z tasksetami')
                        showTasksetArrayImportTable(jsonStr);
                    else {
                        if ( debug )
                            console.warn("To nie powinno się wydarzyć! pewnie nie pusty plik json bez danych");
                    }

                } else {
                    deps.$("#"+name+"InvalidBadFile").show();
                    deps.$("#"+name+"Label").addClass("invalid-file");
                }
                
            });
        }

        var fileInputRefreshAllVisually = (name) => {
                    
            deps.$("#"+name+"InvalidBadFile").hide();
            deps.$("#"+name+"InvalidEmptyFile").hide();
            deps.$("#"+name+"InvalidOldFile").hide();
            deps.$("#"+name+"TableTBody").html("");
            deps.$("#"+name+"Table").addClass('collapse');
            deps.$("#btnSend"+name).addClass('disabled').prop("disabled", true);
            deps.$("#"+name+"Label").removeClass("invalid-file").removeClass("valid-file");
        }

        let isJson = (str) => {
            
            if ( typeof str === "object")
                return true;
            try {
                JSON.parse(str);
                if ( typeof JSON.parse(str) !== 'object')
                    return false;
            } catch (e) {
                console.warn(e);
                return false;
            }
            return true;
        }

        var checkOnDisablingSaveTask = () => {

            if ( deps.$("#saveToTasksetCB")[0].checked) {
                var taskNameInputValue = deps.$("#newTasksetNameInput").val().trim();
                if ( !validateTasksetName(taskNameInputValue) && taskNameInputValue != "") {
                    deps.$("#newTasksetNameInput").removeClass("text-dark").css({"color":"red"});
                    deps.$("#btnSendSaveTask")[0].disabled = true;
                } else if ( dualListLogic.tasksetAlreadyExists(taskNameInputValue) ) {

                    deps.$("#newTasksetNameInput").removeClass("text-dark").css({"color":"red"});
                    deps.$("#btnSendSaveTask")[0].disabled = true;
                } else {
                    deps.$("#newTasksetNameInput").addClass("text-dark").css({"color":"initial"});

                    if ( dualListLogic.getOutput().length > 0 || taskNameInputValue != "")
                        deps.$("#btnSendSaveTask")[0].disabled = false;
                    else
                        deps.$("#btnSendSaveTask")[0].disabled = true;
                }

                if ( validateTasksetName(taskNameInputValue) )
                    dualListLogic.updateSaveToTasksetInfo(1);
                else
                    dualListLogic.updateSaveToTasksetInfo(0);
            } else {
                deps.$("#btnSendSaveTask")[0].disabled = false;
            }
            
        }
        
        var checkOnDisablingSaveTaskInSet = () => {

            var taskNameInpuSetValue = deps.$("#newTasksetNameInputSet").val().trim();
            if ( !validateTasksetName(taskNameInpuSetValue) && taskNameInpuSetValue != "") {
                deps.$("#newTasksetNameInputSet").removeClass("text-dark").css({"color":"red"});
                deps.$("#btnSendSaveTaskset")[0].disabled = true;
            } else if ( Object.keys(self.tasksets).includes(taskNameInpuSetValue)) {
                deps.$("#newTasksetNameInputSet").removeClass("text-dark").css({"color":"red"});
                deps.$("#btnSendSaveTaskset")[0].disabled = true;
            } else  {
                deps.$("#newTasksetNameInputSet").addClass("text-dark").css({"color":"initial"});
                if ( taskNameInpuSetValue != "" )
                    deps.$("#btnSendSaveTaskset")[0].disabled = false;
                else
                    deps.$("#btnSendSaveTaskset")[0].disabled = true;
            }
        }

        self.createNewTaskset = () => {

            var tasksetName = deps.$("#newTasksetNameInputSet").val().trim();
            Ajax.createTaskset(tasksetName, (data) => {

                if ( data === false) {
                    displayInfoAnimation("Nie stworzono zestawu.", false);
                    return;
                }
                displayInfoAnimation("Stworzono nowy zestaw.", true);
                
                deps.$("#newTasksetNameInputSet").val("");
                deps.$("#btnSendSaveTaskset")[0].disabled = true;
                deps.window.location.hash = "/" + tasksetName;
                self.currentEditingTasksetNames = [tasksetName];
                self.updateTasksetsInfo();
            });
        }

        var tooltipsUpdate = () => {
    
            if ( deps.$('[data-toggle="tooltip"]').tooltip !== null && deps.$('[data-toggle="tooltip"]').tooltip !== undefined)
                deps.$('[data-toggle="tooltip"]').tooltip({
                    trigger : 'hover'
                });
        }

        var validateTasksetName = (tasksetName) => {

            var valid = false;
            var found = tasksetName.match(tasksetRegex); 
            if ( found !== null && found[0] === tasksetName)
                valid = true;

            return valid;
        }

        self.changeVariant = (variantString) => {
    
            self.currentTaskNameString = variantString;

            deps.$("#btnSaveEditedTask").hide();
            deps.$("#btnSaveTask").show();
            deps.$(".taskSettingsPanelSidepanelBtn").removeClass("invisible");
    
            self.endDemo();
            
            try {
                self.currentVariant = self.CreatorCore.getVariant(variantString);
                self.changeVisualsForCreatingNewVariant(variantString);
                self.lastEditedTaskID = undefined;
                self.focusedTaskID = undefined;
            } catch (e) {
                if (debug)
                    console.warn(e);
                displayInfoAnimation("Nie udało się zmienić wariantu zadania. Być może wybrano nie istniejący wariant?", false);
            }   
        }
    
        self.downloadImportedTasks = () => {
            Ajax.getImportedTasksFile(
                isJson,
                self.currentEditingTasksetNames[0], 
                (blob) => {

                    if ( blob === false) {
                        displayInfoAnimation("Nie pobrano pliku.", false);
                        return;
                    } else if ( blob === 0){
                        displayInfoAnimation("Nie pobrano pliku, (Czy jesteś zalogowany jako lektor?)", false);
                        return;
                    }

                    const url = deps.window.URL.createObjectURL(blob);
                    const a = $('<a>');
                    a[0].style.display = 'none';
                    a[0].href = url;
                    // the filename you want
                    a[0].download = 'importedTasksFile.json';
                    deps.window.document.body.appendChild(a[0]);
                    a[0].click();
                    deps.window.URL.revokeObjectURL(url);
                    displayInfoAnimation("Pomyślnie pobrano plik.", true);
                });
        }
    
        self.sendTask = (tasksets) => {
            
            if (!self.currentVariant) 
                return false;
            
            self.currentEditingTasksetNames = tasksets;
            self.currentVariant.sendTaskVariantToTasksets(
                Ajax.sendTask,
                self.updateInfoAfterSendingTasks,
                tasksets,
                isJson
            );
        }
    
        self.editTask = (taskID = self.currentlyEditingTaskIds, byClickOnTaskEditButton = false) => {
            
            if (taskID.length == 1)
                Ajax.getImportedTaskByID(
                    taskID[0],
                    (data) => {
                        if ( data === false) {
                            displayInfoAnimation("Problem z pobraniem pliku z serwera.", false);
                            return;
                        }

                        if ( data.taskExists && data.taskExists == "false") {
                            displayInfoAnimation("Nie znaleziono obecnie edytowanego zadania.",false);
                            return;
                        }
                        self.changeVariant(data.taskName);
                        try {
                            self.currentVariant.loadTaskFrom(data);
                            deps.$("#btnSaveEditedTask").show();
                            $("#modalBodyEdit").hide();
                            self.lastEditedTaskID = taskID[0];
                            self.currentlyEditingTaskIds = taskID; 
                            self.changeVisualsForEditing();
                            
                            if ( self.currentEditingTasksetNames.length == 1) {
                                editTasksetsDropdowns(self.currentEditingTasksetNames[0]);
                                if ( byClickOnTaskEditButton )
                                    self.prepareTasksetTableFor(self.tasksets[self.currentEditingTasksetNames[0]],self.currentEditingTasksetNames[0]);
                            } else 
                                editTasksetsDropdowns(false);
                        } catch (e) {
                            if (debug)
                                console.warn(e);
                            displayInfoAnimation("Nie udało się wczytać wariantu zadania, (Czy jesteś zalogowany jako lektor?)", false);
                        }   
                        
                    });
            else if (taskID.length > 1) {
                deps.$("#btnSaveEditedTask").show();
                self.lastEditedTaskID = undefined;
                self.currentlyEditingTaskIds = taskID; 
                self.changeVisualsForEditing();
                self.updateTasksetsInfo(
                    self.setupEditModalForCurrentlyEditingTasksets
                );

            } else {
                if ( debug )
                    console.warn("To nie powinno sie wydarzyć!");
            }
        }
    
        self.changeVisualsForEditing = () => {
    
            //remove
            var previousFromVariantList = deps.$("#main_nav").find("li.bg-success");
            if ( previousFromVariantList.length > 0) {
                previousFromVariantList.removeClass("bg-success");
            }
    
            if ( self.lastEditedTaskID ) {
                var previousFromTable = deps.$("#importedTasksTable").find("tr.bg-info");
                
                if ( previousFromTable.length > 0)
                previousFromTable.removeClass("bg-info")
            }
    
            //add
            if ( self.lastEditedTaskID )
                deps.$(`[data-taskid='${self.lastEditedTaskID}']`)
                .closest("tr").addClass("bg-info");
            
            var taskSettingsPanel = deps.$("#taskSettingsPanel");
            
            var taskVariantNames = Object.keys(self.tablicaPolskichNazwTaskow);
            if ( taskVariantNames.includes(getTaskVariantNameIfExists())) { 
                taskSettingsPanel.removeClass("bg-success").removeClass("text-white");
            } else {
                taskSettingsPanel.addClass("bg-success").addClass("text-white");
            }
            
        }
    
        self.changeVisualsForCreatingNewVariant = (variantString) => {
            
            //remove
            if ( self.lastEditedTaskID ) {
                var previousFromTable = deps.$("#importedTasksTable").find("tr.bg-info");
                
                if ( previousFromTable.length > 0)
                previousFromTable.removeClass("bg-info")
            }
    
            var previousFromVariantList = deps.$("#main_nav").find("li.bg-success");
            if ( previousFromVariantList.length > 0) {
                previousFromVariantList.removeClass("bg-success");
            }
            
            var taskSettingsPanel = deps.$("#taskSettingsPanel");
            if ( variantString && variantString !== "" ) {
                
                deps.$(deps.$("#main_nav").find(`[href*='/lecturer/taskmanager/#${variantString}']`).closest("li")[0]).addClass("bg-success");
                taskSettingsPanel.removeClass("bg-success").removeClass("text-white");
            } else { 
                taskSettingsPanel.addClass("bg-success").addClass("text-white");
            }
        }
    
        /*send edited task*/
        self.saveEditTask = () => {
            
            if (!self.currentVariant) 
                return false;
            
            var taskIDs = [];
            if ( self.lastEditedTaskID )
                taskIDs.push(self.lastEditedTaskID);
            else if ( self.currentlyEditingTaskIds.length ) 
                taskIDs = self.currentlyEditingTaskIds
                    .filter(tId => {
                        var tasksetName = findTasksetNameForTaskId(tId);

                        if ( $("#editingTaskset"+tasksetName).length ) {

                            if ( $("#editingTaskset"+tasksetName)[0].checked)
                                return true;
                            else
                                return false;
                        } else 
                            return false;
                    });
            else {
                if ( debug )
                    console.warn("To nie powinno się wydarzyć!");
            }

            self.currentVariant.sendEditedTaskVariantToTaskset(
                Ajax.putEditTask,
                self.updateInfoAfterSendingEditedTasks,
                taskIDs
            );
        };
    
        /*delete task*/
        self.deleteTask = (taskID) => {
            Ajax.deleteTask(taskID, self.setupImportedTasksTableAfterDelete);
        }
        
    
        self.findTaskInTableById_AndChangeBGColor = (id, color) => {
    
            var newTrTask = deps.$("#importedTasksElem").find('[data-thistaskid="'+id+'"]');
            newTrTask.addClass("newTask");
        }
    
        self.setupDemo = () => {
            
            if (!self.currentVariant)
                return;
    
            deps.$("#GameWrapperDiv").show();
            deps.$("#taskEditHolder").hide();
            deps.$("#btnDemoTask").hide();
            deps.$("#btnDemoTaskEnd").show();
            deps.$("body").addClass('gameDemoBody');
    
            if (deps.$("#GameDiv").length)
                deps.$("#GameDiv").html("");
    
            var taskToSetup = self.currentVariant.prepareTaskJsonFile();
            try {
                self.currentTaskVariant = self.CreatorCore.getVariant_GameCore(taskToSetup.taskName, self.currentVariant.prepareTaskJsonFile());
            } catch (e) {
                if ( debug )
                    console.warn(e);
            }
            self.resizeWindow();
        }
    
        self.endDemo = () => {
    
            if( self.currentTaskVariant ) 
                self.currentTaskVariant.isTaskDone = true;
            deps.$("#GameWrapperDiv").hide();
            deps.$("#taskEditHolder").show();
            deps.$("#btnDemoTask").show();
            deps.$("#btnDemoTaskEnd").hide();
            deps.$("#gameInstruction").html(`<h2 id="mainTitle1">` +
            ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#mainTitle1", false, false):"Tworzenie zadań") +
            `</h2>`);
            deps.$("body").removeClass('gameDemoBody');
    
            self.resizeWindow();
        }
    
        self.deleteAllTasksFromTableVisually = () => {
            deps.$("#importedTasksElem").html("");
            self.updateTasksetsInfo();
        }
    
        self.resizeWindow = () => {
            if ( self.currentTaskVariant && !self.currentTaskVariant.isTaskDone) {
                deps.$("html").height("100%");
                function isInt(n) {
                    return n % 1 === 0;
                }
                
                var h1 = deps.$(window.document).height();
                if ( isInt (h1))
                    h1 -= 1;
                deps.$("html").height(h1);
            } else {
                deps.$("html").height("100%");
            }
        }
    
        var displayInfoAnimation = (text, success = true) => {
            var previousMessages = $(".failSuccessInfo");
            previousMessages.each((b,t)=>{
                $(t).css({marginTop: '+=50px'});
            });

            var failInfoDiv = deps.$(`<div class="failSuccessInfo alert alert-`+(success?"success":"danger")+`">` + text + `</div>`)
            deps.$("#bigChangeDiv").append(failInfoDiv)
        
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

        self.taskSettingsPanelSetup = () => {
            
            deps.$("#gameInstruction").html(`<h2 id="mainTitle2">` +
            ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#mainTitle2", false, false):"Zarządzanie zestawami") +
            `</h2>`);
            var tasksetSettingsDiv = deps.$("#tasksetSettingsDiv");
            self.changeVisualsForCreatingNewVariant("");
            deps.$(".taskDiv").hide();
            tasksetSettingsDiv.show();
            var taskSettingsPanel = deps.$("#taskSettingsPanel");
            taskSettingsPanel.addClass("bg-success").addClass("text-white");
        }

        var checkIfJsonHasObjectWithTasksetOrArrayOfTasksOrArrayOfTasksets = (json) => {

            if ( json.tasksetContent && json.tasksetName)
                return "jeden taskset";
            if ( json[0] && json[0].taskContent)
                return "array z zadankami";
            if ( json[0] && json[0].tasksetContent)
                return "array z tasksetami";
            else
                return "other";
        }
        self.deleteTaskset = (tasksetName = self.currentEditingTasksetNames[0]) => {

            if ( self.currentEditingTasksetNames.length !== 1) {
                if ( debug )
                    console.warn("To nie powinno się wydarzyć!")
                return;
            }

            Ajax.deleteTaskset(tasksetName, 
                (data) => {
                    if ( data === false ) {
                        displayInfoAnimation("Nie usunięto zestawu " + tasksetName, false);
                        return;
                    }
                    
                    var tasksetTaskIDs = self.tasksets[tasksetName].map(task => task.taskID);
                    self.allTaskIds = self.allTaskIds.filter(t => !tasksetTaskIDs.includes(t));
                    self.oldTaskIds = self.oldTaskIds.filter(t => !tasksetTaskIDs.includes(t));
                    self.newTaskIds = self.newTaskIds.filter(t => !tasksetTaskIDs.includes(t));

                    self.editedTaskIds = self.editedTaskIds.filter(t => !tasksetTaskIDs.includes(t));
                    displayInfoAnimation("Usunięto zestaw " + tasksetName, true);
                    self.setLocationUrlWithTaskset('default');
                    self.currentEditingTasksetNames = ['default'];
                    self.updateTasksetsInfo();
                })
        }

        self.prepareTasksetTasksInfo = () => {
            var multipleTasksetTasksInfo = deps.$('#multipleTasksetTasksInfo');

            multipleTasksetTasksInfo.html(``);
            var nameCounter = {};
            if (self.currentEditingTasksetNames.length != 1 && debug)
                console.warn("To nie powinno się zdarzyć!");

            var tasks = self.tasksets[self.currentEditingTasksetNames[0]];
            for (let i = 0; i < tasks.length; i++) {
                var task = tasks[i];
                var taskName = task.taskName;

                if ( !nameCounter[taskName] )
                    nameCounter[taskName] = 1;
                else
                    nameCounter[taskName]++;
            }

            var taskVariantNames = Object.keys(self.tablicaPolskichNazwTaskow);
            for ( let i = 0; i < taskVariantNames.length; i++) {
                var taskName = taskVariantNames[i];

                var taskNameLong = self.tablicaPolskichNazwTaskow[taskName];

                var tr = $("<tr>");
                var tdIndex = $("<td>");
                var tdName = $("<td>");
                var tdCount = $("<td>");

                tdIndex.text(i+1);
                tdName.html(taskNameLong)
                tdCount.text(nameCounter[taskName]? nameCounter[taskName]: 0);

                tr.append(tdIndex).append(tdName).append(tdCount);
                multipleTasksetTasksInfo.append(tr);
            }

        }

        self.editTasksetNameModalSetup = () => {

            var changeTasksetNameInput = deps.$('#changeTasksetNameInput');
            var btnSendchangeTasksetName = deps.$("#btnSendchangeTasksetName");
            if ( self.currentEditingTasksetNames[0]) {
                changeTasksetNameInput.val(self.currentEditingTasksetNames[0]);
                btnSendchangeTasksetName[0].disabled = true;
                deps.$("#invalidPasswordInfoEmail").hide();
                changeTasksetNameInput.focus(); 
            } else {
                if ( debug )
                    console.warn("To nie powinno się wydarzyć!");
            }
        }

        self.checkSetupButtonAndInformAboutTasksetName = () => {

            var btnSendchangeTasksetName = deps.$("#btnSendchangeTasksetName");
            var changeTasksetNameInput = deps.$('#changeTasksetNameInput');
            var taskNewName = changeTasksetNameInput.val();
            var tasksetNamesArray = Object.keys(self.tasksets);

            if ( tasksetNamesArray.includes(taskNewName) || self.currentEditingTasksetNames.includes(taskNewName) ) {

                btnSendchangeTasksetName[0].disabled = true;
                if ( self.currentEditingTasksetNames.includes(taskNewName) )
                    deps.$("#invalidPasswordInfoEmail").text("Taka sama nazwa jak obecna!");
                else
                    deps.$("#invalidPasswordInfoEmail").text("Nazwy zestawów powtarzają się!");

                deps.$("#invalidPasswordInfoEmail").show();
            }
            else {
                btnSendchangeTasksetName[0].disabled = false;
                deps.$("#invalidPasswordInfoEmail").hide();
            }

        }

        self.editTasksetNameSend = () => {

            var changeTasksetNameInput = deps.$('#changeTasksetNameInput');
            var newTasksetName = changeTasksetNameInput.val();
            var tasksetName = self.currentEditingTasksetNames[0];

            if ( newTasksetName && self.currentEditingTasksetNames.length === 1)
                Ajax.putTasksetName( tasksetName, newTasksetName, 
                    (data) => {
                        if ( data === false ) {
                            displayInfoAnimation("Nie udało się zmienić nazwy z " + tasksetName + " na " + newTasksetName, false);
                            return;
                        }
                        displayInfoAnimation("Udało się zmienić nazwe z " + tasksetName + " na " + newTasksetName, true);
                       
                        self.currentEditingTasksetNames = [newTasksetName];
                        self.setLocationUrlWithTaskset(newTasksetName);
                        self.updateTasksetsInfo();
                    });
            else {
                if ( debug )
                   console.warn("To nie powinno się wydarzyć!");
            }
        }

        var URLHasTaskVariantName = () => {

            var urlVariantAndTaskset = decodeURI(window.location.hash.substr(1)).split("/");
            var urlVariant =  urlVariantAndTaskset[0];
            if ( urlVariant.length != "")
                return true;
            else
                return false;
        }

        var getTaskVariantNameIfExists = () => {

            var urlVariantAndTaskset = decodeURI(window.location.hash.substr(1)).split("/");
            var urlVariant =  urlVariantAndTaskset[0];
            if ( urlVariant.length != "")
                return urlVariant;
            else
                return false;
        }

        var URLHasTasksetName = () => {
            
            var urlVariantAndTaskset = decodeURI(window.location.hash.substr(1)).split("/");
            var urlTaskset =  urlVariantAndTaskset[1];
            if ( urlTaskset )
                return true;
            else
                return false;
        }

        var getTasksetNameIfExists = () => {

            var urlVariantAndTaskset = decodeURI(window.location.hash.substr(1)).split("/");
            var urlTaskset =  urlVariantAndTaskset[1];
            if ( urlTaskset )
                return urlTaskset;
            else
                return 'default';
        }

        self.setupTitleAndButtonsForVariantsPresence = () => {

            deps.$("#mainNavbarDropdown").hasClass("show")?deps.$(`[data-target="#mainNavbarDropdown"]`).click() :null;

            var btnDemoTask = deps.$("#btnDemoTask");
            var btnSaveTask = deps.$("#btnSaveTask");
            var btnDemoTaskEnd = deps.$("#btnDemoTaskEnd");
            var btnDemoTask = deps.$("#btnDemoTask");
            var btnSaveEditedTask = deps.$("#btnSaveEditedTask");
            
            var allVariantNames = Object.keys(self.tablicaPolskichNazwTaskow);
            if ( URLHasTaskVariantName() && !allVariantNames.includes(getTaskVariantNameIfExists())) {
                displayInfoAnimation("Nie znaleziono wariantu zadania.", false);
                self.taskSettingsPanelSetup();
            } else if ( URLHasTaskVariantName() && allVariantNames.includes(getTaskVariantNameIfExists())) {
               
                deps.$("#gameInstruction").html(`<h2 id="mainTitle1">` +
                ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#mainTitle1", false, false):"Tworzenie zadań") +
                `</h2>`);
                btnDemoTask.show();
                btnSaveTask.show();
                deps.$(".taskSettingsPanelSidepanelBtn").removeClass("invisible");
            } else {
                self.endDemo();
                btnDemoTaskEnd.hide();
                btnDemoTask.hide();
                btnSaveTask.hide();

                btnSaveEditedTask.hide();
                deps.$(".taskSettingsPanelSidepanelBtn").addClass("invisible");
                self.taskSettingsPanelSetup();
            }
        }

        var showTasksetModalPart = () => {
            $("#newTasksetNameCol").removeClass("collapse");
            $("#dualList").removeClass("collapse");
            $("#saveToTasksetModalPartHr").removeClass("collapse");
        }

        var hideTasksetModalPart = () => {
            $("#newTasksetNameCol").addClass("collapse");
            $("#dualList").addClass("collapse");
            $("#saveToTasksetModalPartHr").addClass("collapse");
        }

        var getChosenTasksets = () => {

            var taskSets = [];
            saveTaskToChosenTaskset = $("#saveToTasksetCB")[0].checked;

            if ( saveTaskToChosenTaskset ) {
                
                

                var newTasksetName = $("#newTasksetNameInput").val().trim();
                if ( validateTasksetName(newTasksetName))
                    taskSets.push(newTasksetName);

                var duallistTasksets = dualListLogic.getOutput();
                if ( duallistTasksets.includes(newTasksetName))
                    taskSets = []
                if ( duallistTasksets.length )
                    taskSets = deps.$.merge(taskSets, duallistTasksets);

            } else 
                taskSets.push("default");

            return taskSets;
        }

        var prepareDualList = () => {

            dualListLogic.refresh();
            dualListLogic.insertOptions(Object.keys(self.tasksets));

            
            if ( self.currentEditingTasksetNames.length && !(self.currentEditingTasksetNames.length === 1 && self.currentEditingTasksetNames.includes('default') ) ) {
                
                $("#saveToTasksetCB")[0].checked = true;
                showTasksetModalPart();
                dualListLogic.move(self.currentEditingTasksetNames);

            } else {
                $("#saveToTasksetCB")[0].checked = false;
                hideTasksetModalPart();
                
            }
        }

        self.updateInfoAfterSendingEditedTasks = (data, taskID) => {
            if ( data === false) {
                displayInfoAnimation("Nie nadpisano, spróbuj zapisać nowe.", false);
                return;
            }
            
            self.editedTaskIds.push(taskID); 
            displayInfoAnimation("Pomyślnie nadpisano.", true);

            self.updateTasksetsInfo();
        } 

        self.updateInfoAfterSendingTasks = (taskIds) => {

            if ( taskIds === false) {
                displayInfoAnimation("Nie zapisano zadania.", false);
                return;
            } else if (taskIds === 0 ) {
                displayInfoAnimation("Nie zapisano zadania, (Czy jesteś zalogowany jako lektor?)", false);
            } else {
                displayInfoAnimation("Pomyślnie zapisano zadanie.", true);
            }

            self.currentlyEditingTaskIds = taskIds;
            self.lastEditedTaskID = undefined;
            self.updateTasksetsInfo(
                self.editTask
            );
        } //then
        self.updateTasksetsInfo = (afterUpdateCallback) => {
            
            Ajax.getTasksetsInfo(
                isJson,
                (tasksets) => {
                    if (tasksets === false) {
                        displayInfoAnimation("Problem z pobraniem danych ze serwera", false);
                        return;
                    } else if (tasksets === 0) {
                        displayInfoAnimation("Problem z pobraniem danych ze serwera, (Czy jesteś zalogowany jako lektor?)", false);
                        return;
                    }

                    self.tasksets = tasksets;
                    if ( self.currentEditingTasksetNames.length <= 1 )
                        self.prepareTasksetTableAfterRequest(true);
                    else
                        self.prepareTasksetTableAfterRequest(false);

                    if ( afterUpdateCallback )
                        afterUpdateCallback();
                }
            )
        } //then
        self.prepareTasksetTableAfterRequest = (FocusedOnSingleTaskset = false) => {

            if ( FocusedOnSingleTaskset ) {
                
                var currentlyEditedTaskset;
                var currentlyEditedTasksetName;
                if ( self.currentEditingTasksetNames.length == 1) {

                    currentlyEditedTaskset = self.tasksets[self.currentEditingTasksetNames[0]];
                    currentlyEditedTasksetName = self.currentEditingTasksetNames[0];
                } else {

                    if ( self.tasksets['default'] ) {
                        currentlyEditedTaskset = self.tasksets['default'];
                        currentlyEditedTasksetName = 'default';

                    } else {
                        if ( debug )
                            console.warn("To nie powinno się wydarzyć!");
                    }
                }
                
                self.prepareTasksetTableFor(currentlyEditedTaskset, currentlyEditedTasksetName);
                
            } else { 
                
                setupNavbarForMultipleTasksets();
                editTasksetsDropdowns(false);
            }
        }

        var showMultipleTasksetsView = () => {

            deps.$("#btnUploadJsonImportedTasks").hide();
            deps.$("#btnDownloadJsonImportedTasks").hide();
            deps.$("#btnDeleteAllTasks").hide();
            deps.$("#importedTasksTable").hide();
            deps.$("#btnDeleteTaskset").hide();
            deps.$("#btnDeleteTaskset").addClass("collapse");
            deps.$("#btnDeleteTasksetSel").hide();
            deps.$("#btnDeleteTasksetSel").addClass("collapse");
            deps.$("#multipleTasksetsTable").show();
        }

        var showSingleTasksetView = () => {
                
            deps.$("#btnUploadJsonImportedTasks").show();
            deps.$("#btnDownloadJsonImportedTasks").show();
            deps.$("#btnDeleteAllTasks").show();
            deps.$("#importedTasksTable").show();

            deps.$("#multipleTasksetsTable").hide();
        }

        var setupNavbarForMultipleTasksets = () => {
            
            showMultipleTasksetsView();

            var tasksetsTable = deps.$("#multipleTasksetsTableElem");
            tasksetsTable.html("");

            var taskSetNames = self.currentEditingTasksetNames;
            for (let i = 0; i < taskSetNames.length; i++) {
                var taskSetName = taskSetNames[i];

                var tdIndex = deps.$(`<td>`+(i+1)+`</td>`),
                tdName = deps.$(`<td>`),
                tdCount = deps.$(`<td>`),
                tdEdit = deps.$(`<td>
                    <button type="button" class="btn btn-success editButton" data-toggle="modal" data-target="#editTaskModalCenter">`
                    + ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#importedTasksTable button.btn-success",false,false):"Edytuj") +
                    `</button>
                </td>`),
                tdDel = deps.$(`<td>
                    <button type="button" class="btn btn-danger delButton" data-toggle="modal" data-target="#deleteTaskModalCenter">`+
                    ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#importedTasksTable button.btn-danger",false,false):"Usuń")
                    +`</button>
                </td>`);

                tdEdit[0].dataset["taskset"] = taskSetName;
                tdDel[0].dataset["taskset"] = taskSetName;

                tdName.append(window.document.createTextNode(taskSetName));
                tdCount.append(self.tasksets[taskSetName].length);

                var tr = deps.$('<tr>').append(tdIndex).append(tdName).append(tdCount);

                tasksetsTable.append(tr);
            }
        }

        self.setupImportedTasksTableAfterDelete = (data, taskID) => {
            if ( data === false) { 
                displayInfoAnimation("Nie usunięto zadania.", false);
                return;
            }
            
            self.allTaskIds = self.allTaskIds.filter(t => t !== taskID);
            self.oldTaskIds = self.oldTaskIds.filter(t => t !== taskID);
            self.newTaskIds = self.newTaskIds.filter(t => t !== taskID);
            self.editedTaskIds = self.editedTaskIds.filter(t => t !== taskID);

            self.currentlyEditingTaskIds = self.currentlyEditingTaskIds.filter(t => t !== taskID);
            
            if ( self.lastEditedTaskID === taskID) {

                self.focusedTaskID = undefined;
                self.lastEditedTaskID = undefined;
                if(deps.$("#btnSaveEditedTask").length > 0)
                    deps.$("#btnSaveEditedTask").hide();
            }
            displayInfoAnimation("Pomyślnie usunięto.", true);

            self.updateTasksetsInfo();
        }

        self.prepareTasksetTableFor = (tasksetTasksArray, tasksetName) => {

            if ( !tasksetName && debug)
                console.warn("To nie powinno się wydarzyć");
            showSingleTasksetView();

            if ( tasksetName === 'default') {
                deps.$("#btnDeleteTaskset").hide();
                deps.$("#btnDeleteTaskset").addClass("collapse");
                deps.$("#btnDeleteTasksetSel").hide();
                deps.$("#btnDeleteTasksetSel").addClass("collapse");
            } else {
                deps.$("#btnDeleteTaskset").show();
                deps.$("#btnDeleteTaskset").removeClass("collapse");
                deps.$("#btnDeleteTasksetSel").show();
                deps.$("#btnDeleteTasksetSel").removeClass("collapse");
            }
            
            var currentlyEditedTasksetName = tasksetName;
            var foundTaskIds = [];
            var importedTasksArray = tasksetTasksArray; 
            var tableElem = deps.$("#importedTasksElem");
            tableElem.html("");

            for (let i = 0; i < importedTasksArray.length; i++) {
                var task = importedTasksArray[i];
                
                foundTaskIds.push(task.taskID);
                var specialClass = '';

                if (self.currentlyEditingTaskIds.includes(task.taskID))
                    specialClass = 'class="bg-info"';
                var tdIndex = deps.$(`<td>`+(i+1)+`</td>`),
                tdName = deps.$(`<td>`),
                tdDate = deps.$(`<td>`),
                tdEdit = deps.$(`<td>
                    <button type="button" class="btn btn-success editButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#editTaskModalCenter">`
                    + ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#importedTasksTable button.btn-success",false,false):"Edytuj") +
                    `</button>
                </td>`),
                tdDel = deps.$(`<td>
                    <button type="button" class="btn btn-danger delButton" data-taskID="`+task.taskID+`" data-toggle="modal" data-target="#deleteTaskModalCenter">`+
                    ((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("#importedTasksTable button.btn-danger",false,false):"Usuń")
                    +`</button>
                </td>`);

                tdName.append(self.tablicaPolskichNazwTaskow[task.taskName] + (debug?" (" + task.taskName + `)`:""));
                tdDate.append(window.document.createTextNode(task.creationDate));

                var tr = deps.$('<tr '+specialClass+' data-thisTaskId="'+task.taskID+'">').append(tdIndex).append(tdName).append(tdDate).append(tdEdit).append(tdDel);

                tableElem.append(tr);
            }
            
            

            self.newTaskIds = [...foundTaskIds.filter(t=>!self.allTaskIds.includes(t))];
            self.allTaskIds = [...self.allTaskIds, ...self.newTaskIds];
            
            var tasksToHighlit = self.allTaskIds.filter(t=>!self.oldTaskIds.includes(t));
            tasksToHighlit = [...tasksToHighlit, self.editedTaskIds.filter(t=>!tasksToHighlit.includes(t))];
            for (let taskID of tasksToHighlit) {
                self.findTaskInTableById_AndChangeBGColor(taskID);
            }

            if ( self.newTaskIds.length == 1) {
                var oneTaskEdited = self.newTaskIds[0];
                if ( self.currentlyEditingTaskIds.includes(oneTaskEdited)) {
                    
                    self.recentlyAddedTaskId = oneTaskEdited;
                    self.editTask([self.recentlyAddedTaskId]);
                } else {
                    if (debug)
                        console.warn("To nie powinno sie wydarzyć!");
                }
            } else {
                if ( self.lastEditedTaskID ) {
                    if ( self.allTaskIds.includes(self.lastEditedTaskID))
                        self.editTask([self.lastEditedTaskID]);
                }
                self.recentlyAddedTaskId = undefined;
            }

            editTasksetsDropdowns(currentlyEditedTasksetName);

            deps.$(".editButton").on("click",(e)=>{
                var taskID = deps.$(e.target).data("taskid");
                deps.$("#btnSendEditTask").data("taskid",taskID);
                self.focusedTaskID = taskID;
            });
    
            deps.$(".delButton").on("click",(e)=>{
                var taskID = deps.$(e.target).data("taskid");
    
                deps.$("#btnSendDeleteTask").data("taskid",taskID);
                self.focusedTaskID = taskID;
            });
        }

        self.setupEditModalForCurrentlyEditingTasksets = () => {

            
            $("#modalBodyEdit").show();

            var tableBody = $("#multipleTasksetsTableElemEditing");
            tableBody.html('');

            for ( let i = 0; i < self.currentlyEditingTaskIds.length; i++) {
                var taskID = self.currentlyEditingTaskIds[i];

                var tasksetName = findTasksetNameForTaskId(taskID);
                if ( !tasksetName ) {
                    if ( debug )
                        console.warn("To nie powinno się wydarzyć!");
                    continue;
                }
                
                var tr = $("<tr>"),
                tdIndex = $("<td>"),
                tdName = $("<td>"),
                tdCount = $("<td>"),
                tdCB = $("<td>");

                tdIndex.append(i);
                tdName.append(deps.window.document.createTextNode(tasksetName));
                tdCount.append(self.tasksets[tasksetName].length);

                var CB = $(`<div class="custom-control custom-switch">`);
                var CBInput = $(`<input type="checkbox" class="custom-control-input" id="editingTaskset`+tasksetName+`">`);
                var CBLabel = $(`<label class="custom-control-label" for="editingTaskset`+tasksetName+`"></label>`);
                CB.append(CBInput).append(CBLabel);
                CBInput[0].checked = true;
                CBInput[0].dataset["taskset"] = tasksetName;
                tdCB.append(CB);

                tr.append(tdIndex).append(tdName).append(tdCount).append(tdCB);
                tableBody.append(tr);
            }
        }

        var findTasksetNameForTaskId = (taskID) => {

            var tasksetKeys = Object.keys(self.tasksets);
            var found = false;
            for ( let i = 0; i < tasksetKeys.length; i++) {
                var tasksetName = tasksetKeys[i];
                var taskset = self.tasksets[tasksetName];

                for ( let j = 0; j < taskset.length; j++) {
                    var task = taskset[j];

                    if ( task.taskID === taskID) {
                        found = tasksetName;
                        break;
                    }
                }
                if ( found )
                    break;
            }

            return found;
        }

        var editTasksetsDropdowns = (currentlyEditedTasksetName = false) => {
            
    
            var tasksetsDropdown = deps.$("#tasksetsDropdown");
            var tasksetsMoveDropdown = deps.$("#tasksetsMoveDropdown");
            var tasksetsSettingTable = deps.$("#tasksetsSettingTable");
            tasksetsSettingTable.html('');
            var openNavButton = deps.$("#openNavButton");
            var infoNavBtn = deps.$("#infoNavBtn");

            var currentTasksetInfo;
            var headerTasksetNameInfo;
            var headerTasksetCountInfo;
            if ( currentlyEditedTasksetName ) {
                var currentlyEditedTasksetTaskCount = self.tasksets[currentlyEditedTasksetName].length;
                currentTasksetInfo = currentlyEditedTasksetName+": "+currentlyEditedTasksetTaskCount;
                headerTasksetNameInfo = currentlyEditedTasksetName;
                headerTasksetCountInfo = currentlyEditedTasksetTaskCount;
            } else {
                currentTasksetInfo = "Obecnie edycja kilku";
                headerTasksetNameInfo = currentTasksetInfo;

                headerTasksetCountInfo = 0;
                for ( let i = 0; i < self.currentEditingTasksetNames.length; i++) {
                    var tasksetName = self.currentEditingTasksetNames[i];
                    headerTasksetCountInfo += self.tasksets[tasksetName].length;
                }
            }

            
                
            openNavButton.html("")
            openNavButton.text(((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("tasksets"):"☰ Zestawy") +
            ` (`+currentTasksetInfo+`)`);

            infoNavBtn.text(((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("tasksets2"):"Zestawy") +
            ` (`+currentTasksetInfo+")");
            $("#currentTasksetNameSidepanelHeader").text(" "+headerTasksetNameInfo);
            $("#tasksetSettingName").text(headerTasksetNameInfo);

            $("#currentTasksetTaskCountSidepanelHeader").text(((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("taskCount"):"Ilość zadań: ")+headerTasksetCountInfo);


            tasksetsDropdown.html(`
                <div class="dropdown-item">Zestawy do wyboru:</a>
                <div class="dropdown-divider"></div>
            `);
            tasksetsMoveDropdown.html(``);

            var tasksetsKeys = Object.keys(self.tasksets);
            for ( let i = 0; i < tasksetsKeys.length; i++) {
                var tasksetName = tasksetsKeys[i];
                var taskset = self.tasksets[tasksetName];

                var mapOfAllTaskIdFromTaskset = taskset.map(task => task.taskID);
                var hasTaskThatIsBeingEdited = false;
                for ( let j = 0; j < self.currentlyEditingTaskIds.length; j++) {
                    var taskID = self.currentlyEditingTaskIds[j];
                    if ( mapOfAllTaskIdFromTaskset.includes(taskID) ) {
                        hasTaskThatIsBeingEdited = true;
                        break;
                    }
                }

                var specialClass = ``;
                if ( tasksetName === 'default')
                    specialClass = `bg-secondary`;
                if ( currentlyEditedTasksetName ) {
                    if ( tasksetName === currentlyEditedTasksetName)
                        specialClass = `bg-success`;
                    else if ( self.currentEditingTasksetNames.includes(tasksetName) || hasTaskThatIsBeingEdited)
                        specialClass = `bg-info`;
                } else if ( self.currentEditingTasksetNames.includes(tasksetName) || hasTaskThatIsBeingEdited) {
                    specialClass = `bg-info`;
                }

                var elem = deps.$(`<a class="dropdown-item `+specialClass+`">`);
                elem[0].dataset["taskname"] = tasksetName;
                elem.text(tasksetName + " (zadań: " + taskset.length + ")");

                var elemMove = deps.$(`<a class="dropdown-item `+specialClass+`">`);
                elemMove[0].dataset["taskname"] = tasksetName;
                elemMove.text(tasksetName + " (zadań: " + taskset.length + ")");

                var elemSettTr = deps.$(`<tr `+(specialClass?`class="`+specialClass+`"`:'')+`>`);
                elemSettTr[0].dataset["taskname"] = tasksetName;
                var elemSettTdIndex = deps.$(`<td>`);
                elemSettTdIndex.append(i+1);
                var elemSettTdName = deps.$(`<td>`);
                elemSettTdName.append(window.document.createTextNode(tasksetName));
                var elemSettTdCount = deps.$(`<td>`);
                elemSettTdCount.append(taskset.length);

                var elemSettTdButt = deps.$(`<td>`);
                if ( specialClass != `bg-success` ) {
                    var elemSettTdButtElem = deps.$(`<button class="btn btn-success">`)
                    elemSettTdButtElem.text(((typeof deps.PageLanguageChanger() != "undefined")?deps.PageLanguageChanger().getTextFor("choose"):"Wybierz"));
                    elemSettTdButtElem[0].dataset["taskname"] = tasksetName;
                    elemSettTdButtElem.on('click',(e)=> {
                        var thisTaskName = e.target.dataset["taskname"];
                        self.openTaskset(thisTaskName);
                    });
                    elemSettTdButt.append(elemSettTdButtElem);
                }

                elemSettTr.append(elemSettTdIndex).append(elemSettTdName).append(elemSettTdCount).append(elemSettTdButt);
                
                tasksetsDropdown.append(elem);
                tasksetsMoveDropdown.append(elemMove);
                tasksetsSettingTable.append(elemSettTr);
                
                elem.on('click', (e) => {
                    
                    var thisTaskName = e.target.dataset["taskname"];
                    self.openTaskset(thisTaskName);
                });

                elemMove.on('click', (e) => {
                    
                    var thisTaskName = e.target.dataset["taskname"];
                    
                    self.moveFocusedTaskToTaskset(thisTaskName);
                });
            }
        }

        self.moveFocusedTaskToTaskset = (tasksetName) => {

            if ( !self.focusedTaskID && debug)
                console.warn("To nie powinno się wydarzyć!");

            Ajax.putTaskToTaskset(tasksetName, self.focusedTaskID, (data)=> {

                if ( data === false) {
                    displayInfoAnimation("Nie udało się przenieść zadania do " + tasksetName, false);
                    return;
                } 
                displayInfoAnimation("Udało się przenieść z zadanie do " + tasksetName, true);

                self.editedTaskIds.push(self.focusedTaskID);
                self.oldTaskIds = self.oldTaskIds.filter(id => id != self.focusedTaskID);
                self.updateTasksetsInfo();
                $('#editTaskModalCenter').modal('hide');
            });
        }


        self.openTaskset = (tasksetName) => {

            if (!self.tasksets[tasksetName]) {
                displayInfoAnimation("Zestaw nie istnieje!.", false);
                return;
            }

            self.currentEditingTasksetNames = [tasksetName];
            self.prepareTasksetTableFor(self.tasksets[tasksetName], tasksetName);
            self.prepareVariantDropdownUrls(tasksetName);
            self.prepareEditTasksetNameButton();
            self.setLocationUrlWithTaskset(tasksetName);
        }

        self.prepareEditTasksetNameButton = () => {

            var btnchangeTasksetNameModalCenter = deps.$("#btnchangeTasksetNameModalCenter");
            var btnchangeTasksetNameModalCenterSel = deps.$("#btnchangeTasksetNameModalCenterSel");
            if ( self.currentEditingTasksetNames.length === 1 && !self.currentEditingTasksetNames.includes("default")){
                btnchangeTasksetNameModalCenter.show();
                btnchangeTasksetNameModalCenter.removeClass('collapse');
                btnchangeTasksetNameModalCenterSel.show();
                btnchangeTasksetNameModalCenterSel.removeClass('collapse');
            }else {
                btnchangeTasksetNameModalCenter.hide();
                btnchangeTasksetNameModalCenter.addClass('collapse');
                btnchangeTasksetNameModalCenterSel.hide();
                btnchangeTasksetNameModalCenterSel.addClass('collapse');
            }
        }
        

        self.prepareVariantDropdownUrls = (tasksetName) => {

            var dropDownMenu = $("#mainVariantDropdownMenu");

            var elementsToChange = dropDownMenu.find("a[href]");

            for ( let i = 0; i < elementsToChange.length; i++) {
                var elem = $(elementsToChange[i]);
                
                var currentHrefValue =  elem.attr("href");
                elem.attr("href", tasksetUrlChangeFromTo(currentHrefValue, tasksetName));
            }
        }

        self.setLocationUrlWithTaskset = (tasksetName) => {
            var currentLocation = deps.window.location.href;
            deps.window.location.replace(tasksetUrlChangeFromTo(currentLocation, tasksetName));
        }

        var tasksetUrlChangeFromTo = (currentUrl, tasksetName) => {

            var ret = "";

            var currentHrefValue =  currentUrl;
            var hashSplit = currentHrefValue.split("#");
            if ( hashSplit.length === 1) {
                ret = currentHrefValue+"#/"+tasksetName;
            } else if (  hashSplit.length === 2 ) {
                var slashSplit = hashSplit[1].split("/");
                if (slashSplit.length === 1) {
                    ret = currentHrefValue + "/" + tasksetName;
                } else {
                    ret = hashSplit[0] + "#" + slashSplit[0] + "/" + tasksetName;
                }
            } else {
                if ( debug )
                    console.warn("To nie powinno sie wydarzyć!")
            }

            return ret;
        }
        
        /* Other: */
        
        var otherFrontendLogic = () => {
            
            var observe;
            if (window.attachEvent) {
                observe = function (element, event, handler) {
                    element.attachEvent('on'+event, handler(element));
                };
            }
            else {
                observe = function (element, event, handler) {
                    element.addEventListener(event, handler(element), false);
                };
            }
            function textareaAutoscroll () {
                var allText = deps.$(window.document).find(".taskTextTextarea");
                
                
                var resize = function(text) {
                    return function curried_func(e) {
                        text.style.height = 'auto';
                        text.style.height = text.scrollHeight+'px';
                    }
                }
    
                var delayedresize = function(text) {
                    return function curried_func(e) {
                        deps.window.setTimeout(resize(text), 0);
                    }
                }
                for ( let i = 0; i < allText.length; i++) {
                    var text = allText[i];
                    
                    observe(text, 'change',  resize);
                    observe(text, 'focus',  resize);
                    observe(text, 'cut',     delayedresize);
                    observe(text, 'paste',   delayedresize);
                    observe(text, 'drop',    delayedresize);
                    observe(text, 'keydown', delayedresize);
                
                    text.focus();
                    text.select();
                    resize(text);
                }
            }
            deps.$(window.document).ready(function(){
                textareaAutoscroll();
            });
    
            /* collapse side-panel*/
            self.isNavOpened = false;
    
            /* Set the width of the sidebar to 250px (show it) */
            function openNav() {
    
                deps.window.document.getElementById("mySidepanel").style.width = "";
                var gameInstruction = deps.$("#gameInstruction");
                if (gameInstruction.length > 0) {
                    if (!gameInstruction.hasClass("sidepanelClassForGameInstructionH2")) {
                        gameInstruction.addClass("sidepanelClassForGameInstructionH2");
                    }
                }
                var taskDivs = deps.$(".taskDiv");
                if (taskDivs.length > 0) {
                    if (!taskDivs.hasClass("sidepanelClassForBottonAndTasks")) {
                        taskDivs.addClass("sidepanelClassForBottonAndTasks");
                    }
                }
    
                var bottomDiv = deps.$("#gameBottom");
                if (bottomDiv.length > 0) {
                    if (!bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                        bottomDiv.addClass("sidepanelClassForBottonAndTasks");
                    }
                }
    
                self.isNavOpened = true;
                deps.$(".dropdown-menu").addClass("SideNavOpenClass");
                deps.$(".nav-item .submenu").addClass("SideNavOpenClass");
                deps.$(".nav-item .submenu-left").addClass("SideNavOpenClass");
                deps.$(".taskDiv").addClass("SideNavOpenClass");
                deps.$("#mySidepanel").removeClass("closed");
            }
            
            /* Set the width of the sidebar to 0 (hide it) */
            function closeNav() {
    
                deps.window.document.getElementById("mySidepanel").style.width = "0";
                
                var gameInstruction = deps.$("#gameInstruction");
                if (gameInstruction.length > 0) {
                    if (gameInstruction.hasClass("sidepanelClassForGameInstructionH2")) {
                        gameInstruction.removeClass("sidepanelClassForGameInstructionH2");
                    }
                }
                var taskDivs = deps.$(".taskDiv");
                if (taskDivs.length > 0) {
                    if (taskDivs.hasClass("sidepanelClassForBottonAndTasks")) {
                        taskDivs.removeClass("sidepanelClassForBottonAndTasks");
                    }
                }
    
                var bottomDiv = deps.$("#gameBottom");
                if (bottomDiv.length > 0) {
                    if (bottomDiv.hasClass("sidepanelClassForBottonAndTasks")) {
                        bottomDiv.removeClass("sidepanelClassForBottonAndTasks");
                    }
                }

                self.isNavOpened = false;
                deps.$(".dropdown-menu").removeClass("SideNavOpenClass");
                deps.$(".nav-item .submenu").removeClass("SideNavOpenClass");
                deps.$(".nav-item .submenu-left").removeClass("SideNavOpenClass");
                deps.$(".taskDiv").removeClass("SideNavOpenClass");
                deps.$("#mySidepanel").addClass("closed");
            }
    
            deps.$("#closeNavButton").on("click", ()=> {
                closeNav();
            })
    
            deps.$("#openNavButton").on("click", ()=> {
                openNav();
            })
        }
        /*  initalization  */
        TaskCreatorLecturerLogicInit(data);
         
        return self;
    }
    
    TaskCreatorLecturerLogic.getInstance = (dataLazy, successfulCreationCallback) => {
    
        if (TaskCreatorLecturerLogic.singleton)
            return TaskCreatorLecturerLogic.singleton;

        if ( dataLazy )
            return TaskCreatorLecturerLogic(dataLazy, successfulCreationCallback);
        else {
            return Promise.all([Ajax.getWhoAmI(),Ajax.getAccountInfo(),Ajax.getTasksetsInfo()]).then((values)=>{
                
                if ( !values[0] || !values[1] || !values[2] ){
                    if ( successfulCreationCallback )
                        successfulCreationCallback(false);
                    return;
                }
                var playerInfo = values[0];
                var accountInfo = values[1];
                var tasksets = values[2];
                

                var data = {
                    account:{
                        playerInfo: playerInfo,
                        accountInfo, accountInfo,
                    },
                    tasksets: tasksets
                }

                TaskCreatorLecturerLogic.singleton = TaskCreatorLecturerLogic(data, successfulCreationCallback);

                return TaskCreatorLecturerLogic.singleton;
            }).catch((e) => {
                console.warn("failed");
                console.warn(e);
                if ( successfulCreationCallback )
                    successfulCreationCallback(false);
                // all requests finished but one or more failed
            });
        }
    }
    
    return {
        TaskCreatorLecturerLogic: TaskCreatorLecturerLogic,
        getInstance: TaskCreatorLecturerLogic.getInstance
    }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {TaskCreatorLecturerModule};