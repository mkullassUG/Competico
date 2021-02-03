const TaskCreatorLogic = (playerInfo_, debug) => {
    var self = {};
    self.playerInfo = playerInfo_;
    self.debug = debug;
    self.currentVariant;
    /*       logic variables          */

    /*       logic functions          */
    self.taskCreatorInit = () => {
        /* TODO
        przygotuj start strony
         */
    }

    self.checkIfTaskReady = () => {
        /*TODO:
        sprawdzaj czy obecny wariant przeszedł wymogi zgodności do importu
        */
    }

    self.prepareTaskJsonFile = () => {
        /*TODO:
        spakuj gotowe zadanie do pliku*/
    }

    self.setupDemoFromCurrent = () => {
        /*TODO:
        podgląd stworzonego zadania jako gry*/
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
                break;
            case "ChronologicalOrder":
                break;
            case "WordConnect":
                break;
           default:
               break;
        }

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

/* textarea wtf it is blurry fix*/
var observe;
if (window.attachEvent) {
    observe = function (element, event, handler) {
        element.attachEvent('on'+event, handler);
    };
}
else {
    observe = function (element, event, handler) {
        element.addEventListener(event, handler, false);
    };
}
function textareaAutoscroll () {
    var text = document.getElementById('wordFillDivTaskText');
    function resize () {
        text.style.height = 'auto';
        text.style.height = text.scrollHeight+'px';
    }
    /* 0-timeout to get the already changed text */
    function delayedResize () {
        window.setTimeout(resize, 0);
    }
    observe(text, 'change',  resize);
    observe(text, 'cut',     delayedResize);
    observe(text, 'paste',   delayedResize);
    observe(text, 'drop',    delayedResize);
    observe(text, 'keydown', delayedResize);

    text.focus();
    text.select();
    resize();
}
$(document).ready(function(){
    textareaAutoscroll();
});


/*  dropdown menu   */
$(document).on('click', '.dropdown-menu', function (e) {
    e.stopPropagation();
});
  
  // make it as accordion for smaller screens
if ($(window).width() < 992) {
    $('.dropdown-menu a').click(function(e){
        e.preventDefault();
            if($(this).next('.submenu').length){
                $(this).next('.submenu').toggle();
            }
            $('.dropdown').on('hide.bs.dropdown', function () {
            $(this).find('.submenu').hide();
        })
    });
}

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