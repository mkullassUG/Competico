const TaskCreatorLogic = (playerInfo_, debug) => {
    var self = {};
    /*       logic variables          */
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.currentVariant;

    /*       logic functions          */
    self.taskCreatorInit = () => {
        /* TODO
        przygotuj start strony
        */
        var urlVariant =  window.location.hash.substr(1);
        if ( urlVariant.length != "")
            self.changeVariant(urlVariant);
        else
            self.changeVariant("WordFIll");
    }

    self.changeVariant = (variantString) => {
        /*TODO:
        -sprawdzanie czy wpisano coś w pola obecnego wariantu
            -jesli tak to zapytanie czy na pewno...    
        -zmiana wariantu do edytowania na przycisk
        */

        /*warianty:
            1 - Wypełnianie luk w tekście   (WordFIll)
                1.2 - Jeden wielozdaniowy tekst, jedna pula odpowiedzi
            2 - Łączenie słów i zwrotów z dwóch kolumn (WordConnect)
            3 - Układanie zdań w porządek chronologiczny (ChronologicalOrder)
        */

        //sprawdzanie
        switch (variantString) {
            case "WordFIll":
                self.currentVariant = WordFillCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "WordFill",
                    "taskContent" : {
                      "id" : "4e8f1bec-07ad-4a2e-a0d6-bf225ca91aa1",
                      "instruction" : "Complete the text with the missing words:",
                      "tags" : [ ],
                      "content" : {
                        "id" : "5b3c7a43-c0e6-41bc-8de2-27fcb2a10c0a",
                        "text" : [ "I’m sorry to have to tell you that there has been some ", " in the project and we won’t be able to ", " our original ", " on July 30th for completing the ", " of the new software. Pedro’s absence for three weeks caused a bit of a ", ", and there were more delays when we realised that there was still some ", " in the databases that needed cleaning up. Still, I am confident that we can complete the project by the end of next month." ],
                        "emptySpaces" : [ {
                          "answer" : "slippage"
                        }, {
                          "answer" : "stick to"
                        }, {
                          "answer" : "deadline"
                        }, {
                          "answer" : "rollout"
                        }, {
                          "answer" : "bottleneck"
                        }, {
                          "answer" : "dirty data"
                        } ],
                        "startWithText" : true,
                        "possibleAnswers" : [ "bottleneck", "deadline", "dirty data", "migrate", "rollout", "slippage", "stick to", "within", "scope" ]
                      },
                      "difficulty" : 100.0
                    }
                  });
                break;
            case "ChronologicalOrder":
                self.currentVariant = ChronologicalOrderCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "ChronologicalOrder",
                    "taskContent" : {
                      "id" : "f10298c5-7e1f-4961-ab07-1fe9410bb433",
                      "instruction" : "Put the phrases in order:",
                      "tags" : [ ],
                      "sentences" : [ "Try to understand the problem and define the purpose of the program.", "Once you have analysed the problem, define the successive logical steps of the program.", "Write the instructions in a high-level language of your choice.", "Once the code is written, test it to detect bugs or errors.", "Debug and fix errors in your code.", "Finally, review the program’s documentation." ],
                      "difficulty" : 100.0
                    }
                  });
                break;
            case "WordConnect":
                self.currentVariant = WordConnectCreator();
                self.currentVariant.loadTaskFrom({
                    "taskName" : "WordConnect",
                    "taskContent" : {
                      "id" : "00cb1efa-d388-4647-a5ec-080755b11712",
                      "instruction" : "Match the words with their translations:",
                      "tags" : [ ],
                      "leftWords" : [ "data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier" ],
                      "rightWords" : [ "eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna" ],
                      "correctMapping" : {
                        "0" : 0,
                        "1" : 1,
                        "2" : 2,
                        "3" : 3,
                        "4" : 4,
                        "5" : 5,
                        "6" : 6,
                        "7" : 7,
                        "8" : 8,
                        "9" : 9
                      },
                      "difficulty" : 100.0
                    }
                  });
                break;
           default:
               console.warn("TODO, to nie powinno się wydarzyć!")
               break;
        }

    }

    self.downloadAllTasks = () => {
        self.ajaxGetAllTasks();
    }

    self.downloadImportedTasks = () => {
        self.ajaxGetImportedTasks();
    }

    self.sendTask = () => {
        /*TODO*/
        if (!self.currentVariant) 
            return false;
        
        self.currentVariant.sendTaskVariant(self.sendAjaxTask);
    }

    /*       event listeners          */
    if ($("#btnChronologicalOrder").length)
        $("#btnChronologicalOrder").on("click",()=>{
            if (self.debug)
                console.log("btnChronologicalOrder");
            self.changeVariant("ChronologicalOrder");
        });
    if ($("#btnWordFIll").length)
        $("#btnWordFIll").on("click",()=>{
            if (self.debug)
                console.log("btnWordFIll");
            self.changeVariant("WordFIll");
        });
    if ($("#btnWordConnect").length)
        $("#btnWordConnect").on("click",()=>{
            if (self.debug)
                console.log("btnWordConnect");
            self.changeVariant("WordConnect");
        });
    if ($("#btnDownloadJsonAllTasks").length)
        $("#btnDownloadJsonAllTasks").on("click",()=>{
            if (self.debug)
                console.log("btnDownloadJsonAllTasks");
            self.downloadAllTasks();
        });
    if ($("#btnDownloadJsonImportedTasks").length)
        $("#btnDownloadJsonImportedTasks").on("click",()=>{
            if (self.debug)
                console.log("btnDownloadJsonImportedTasks");
            self.downloadAllTasks();
        });   
    if ($("#btnSendSaveTask").length)
        $("#btnSendSaveTask").on("click",()=>{
            if (self.debug)
                console.log("btnSendSaveTask");
            self.sendTask();
        });
    /* Ajax requests*/
    self.ajaxGetAllTasks = () => {
        /*pobiera taski 
        /api/v1/tasks/all/json/file*/
        // $.ajax({
        //     type     : "GET",
        //     cache    : false,
        //     url      : "/api/v1/tasks/all/json/file",
        //     contentType: "application/json",
        //     success: function(data, textStatus, jqXHR) {
        //         if (self.debug) {
        //             console.log("ajaxGetAllTasks success");
        //             console.log(data);
        //         }


        //     },
        //     error: function(jqXHR, status, err) {
        //         if (self.debug) {
        //         console.warn("ajaxGetAllTasks error");
        //         console.warn(jqXHR);
        //         console.warn(status);
        //         console.warn(err);
        //         }
        //     }
        // });

        fetch('/api/v1/tasks/all/json/file')
            .then(resp => resp.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                // the filename you want
                a.download = 'tasksFile.json';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                alert('your file has downloaded!'); // or you know, something with better UX...
            })
            .catch(() => alert('oh no!'));
    }

    self.ajaxGetImportedTasks = () => {
        /*/api/v1/tasks/imported/json GET - lista wszystkich wprowadzonych zadań*/
        // $.ajax({
        //     type     : "GET",
        //     cache    : false,
        //     url      : "/api/v1/tasks/imported/json/file",
        //     contentType: "application/json",
        //     success: function(data, textStatus, jqXHR) {
        //         if (self.debug) {
        //             console.log("ajaxGetImportedTasks success");
        //             console.log(data);
        //         }
        //     },
        //     error: function(jqXHR, status, err) {
        //         if (self.debug) {
        //         console.warn("ajaxGetImportedTasks error");
        //         console.warn(jqXHR);
        //         console.warn(status);
        //         console.warn(err);
        //         }
        //     }
        // });

        fetch('/api/v1/tasks/imported/json/file')
            .then(resp => resp.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                // the filename you want
                a.download = 'importedTasksFile.json';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                alert('your file has downloaded!'); // or you know, something with better UX...
            })
            .catch(() => alert('oh no!'));
    }
    
    self.ajaxGetNumberOfTasks = (callback) =>{
        /*/api/v1/tasks/imported/count GET - liczba wszystkich wprowadzonych zadań*/
        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/tasks/imported/count",
            contentType: "application/json",
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("ajaxGetNumberOfTasks success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("ajaxGetNumberOfTasks error");
                console.warn(jqXHR);
                console.warn(status);
                console.warn(err);
                }
            }
        });
    }
    /*     ajax http actions       */
    self.sendAjaxTask = (task, callback) => {
        /* /api/v1/tasks/imported POST - dodanie nowego zadania przez JSON */
        var send = task;
        console.log(send);
        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "/api/v1/tasks/imported",
            contentType: "application/json",
            data     : send,
            success: function(data, textStatus, jqXHR) {
                if (self.debug) {
                    console.log("sendAjaxTask success");
                    console.log(data);
                }
                callback(data)
            },
            error: function(jqXHR, status, err) {
                if (self.debug) {
                console.warn("sendAjaxTask error");
                console.warn(jqXHR);
                console.warn(status);
                console.warn(err);
                }
            }
        });
    }

    /*  initalization  */
    self.taskCreatorInit();
     
    return self;
}

TaskCreatorLogic.getInstance = (debug) => {

    if (TaskCreatorLogic.singleton)
        return TaskCreatorLogic.singleton;

    var ajaxReceiveWhoAmI = ( ) => {
        
        $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(playerInfo, textStatus, jqXHR) {
            if (debug){
            console.log("ajaxReceiveWhoAmI success");
            console.log(playerInfo);
            console.log(textStatus);
            console.log(jqXHR);
            }
            TaskCreatorLogic.singleton = TaskCreatorLogic(playerInfo, debug);
            console.log("TaskCreatorLogic");
        },
        error: function(jqXHR, status, err) {
            if (debug){
            console.warn("ajaxReceiveWhoAmI error");
            console.log(data);
            console.log(textStatus);
            console.log(jqXHR);
            }
        }
        });
    }
    
    ajaxReceiveWhoAmI();
    return TaskCreatorLogic.singleton;
}

/* textarea wtf it is blurry (no scroll) fix
nie działa w wordfill bo są inne textarea

WGL to tekst na całej stronei robi się wtedy blurry
*/
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
    //var text = document.getElementById('wordFillDivTaskText');
    var allText = $(document).find(".taskTextTextarea");
    
    //currying concept https://en.wikipedia.org/wiki/Currying
    var resize = function(text) {
        return function curried_func(e) {
            text.style.height = 'auto';
            text.style.height = text.scrollHeight+'px';
        }
    }

    var delayedresize = function(text) {
        return function curried_func(e) {
            window.setTimeout(resize(text), 0);
        }
    }
    for ( let i = 0; i < allText.length; i++) {
        var text = allText[i];
        
        /* 0-timeout to get the already changed text */
        
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
$(document).ready(function(){
    textareaAutoscroll();
});


/*  dropdown menu   */
/*TODO przerobić tak żeby działało jak ja chce przy dynamicznie zmieniającym się ekranie*/
// Prevent closing from click inside dropdown
$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});
  
// make it as accordion for smaller screens
$('.dropdown-menu a').click(function(e){

    if ($(window).width() < 930) { //jak okno jest mniejsze to rozwiń wewnątrz
        e.preventDefault();

        if($(this).next('.submenu').length){
            $(this).next('.submenu').toggle();
        }
        //.one żeby nie zapętlało się niepotrzebnie
        $('.dropdown').one('hide.bs.dropdown', function (e) {
            $(this).find('.submenu').hide();
        })
    } else { //jak okno jest większe to rozwiń na zewnątrz
        if($(this).next('.submenu').length){
            $(this).next('.submenu').toggle();
        }

        $('.dropdown').one('hide.bs.dropdown', function (e) {
            $(this).find('.submenu').hide();
        })
    }
});


/* collapse side-panel*/
/* Set the width of the sidebar to 250px (show it) */
function openNav() {
    document.getElementById("mySidepanel").style.width = "250px";
    // document.getElementById("bigChangeDiv").style.transform = "translateX(250px)"; 
    /*to wtedy zmienie szerokość diva*/
}
  
/* Set the width of the sidebar to 0 (hide it) */
function closeNav() {
    document.getElementById("mySidepanel").style.width = "0";
    // document.getElementById("bigChangeDiv").style.transform = "none"; 
}