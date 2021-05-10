//ListSentenceForming
const ListSentenceForming_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "ListSentenceForming";
    
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
    
        //ROWS nie WORDS
        var sentence_rows = taskData.words;/*[
            ["Let's", "play", "at", "the", "fword", "park", "you", "wword"],
            ["is", "you", "rword"],
            ["hello!", "my", "name", "is", "SUCC"],
            ["buying", "GF", "1000", "gp"],
            ["Harry", "Poter", "went", "to", "Hogward"]
        ];*/
        var DivListSentenceForming;
        var DivRandomizedWords;
        var DivOrderedWords;
        
        //console.log(taskData);
        $("#GameDiv").html("");
        for ( let i = 0; i < sentence_rows.length; i++) {
            var row = sentence_rows[i];
            
            DivListSentenceForming = $(`<div class="Div` + self.taskName + `" id="Div` + self.taskName + `` + i + `">`);
            DivRandomizedWords = $(`<ul class="DivRandomizedWords border-primary connectedSortable`+i+`" id="DivRandomizedWords`+i+`">`);
            for (let j = 0; j < row.length; j++) {
                var word = document.createTextNode(row[j]);

                var li = $(`<li class="` + self.taskName + `word">`);
                li.append(word);
                DivRandomizedWords.append(li);
            }
    
            DivOrderedWords = $(`<ul class="DivOrderedWords border-bottom border-primary text-left connectedSortable`+i+`" id="DivOrderedWords`+i+`">`);
            DivListSentenceForming.append(`<div class="sentenceIndex">` + (i+1) + `</div>`);
            DivListSentenceForming.append(DivRandomizedWords);
            DivListSentenceForming.append(DivOrderedWords);
            
            $("#GameDiv").append(DivListSentenceForming);
            
            function doterHrUpdate ( e, ui ) {

                //może się przydać:
                //https://raw.githubusercontent.com/mattheworiordan/jquery.simulate.drag-sortable.js/master/jquery.simulate.drag-sortable.js

                //$($(".ListSentenceFormingword")[1]).simulateDragSortable({ move: 4 });
                var elmnt = $(e.target).find("." + self.taskName + "word");

                if ( elmnt.length == 1 && $(e.target).hasClass("DivOrderedWords")) {
                    //fast fix
                    $(elmnt[0]).simulateDragSortable({ move: 1 });
                }
            }

            $( `#DivRandomizedWords` + i + `, #DivOrderedWords`+i ).sortable({
                connectWith: `.connectedSortable`+i,
                placeholder: "ui-state-highlight",
                items: "li:not(.placeholder-empty)",
                cancel: "hr",
                remove: doterHrUpdate,
                update: doterHrUpdate,
                // over: doterHrUpdate,
                // out: doterHrUpdate,
                cursor: "grabbing"
            }).disableSelection();
        }
    }
  
    var getAnswersSuper = self.getAnswers;
    self.getAnswers = () => {
        var answers = getAnswersSuper();
        answers = [];
        
        var DivOrderedWords = $(".DivOrderedWords");
        for (let i = 0; i < DivOrderedWords.length; i++) {
            var divListSentenceForming = DivOrderedWords[i];
            answers[i] = [];
            var wordsDivs = $(divListSentenceForming).find("." + self.taskName + "word");
            for ( let j = 0; j <  wordsDivs.length; j++) {
                var divW = $(wordsDivs[j]);

                answers[i][j] = divW.text();
            }
        }

        //co jeśli array nei będzie wystarczającej długości? uzupełnić nullami?

        var DivRandomizedWords  = $(".DivRandomizedWords ");
        for (let i = 0; i < DivRandomizedWords.length; i++) {
            var divListSentenceForming = DivRandomizedWords[i];

            var wordsDivs = $(divListSentenceForming).find("." + self.taskName + "word");
            for ( let j = 0; j <  wordsDivs.length; j++) {
                var divW = $(wordsDivs[j]);

                answers[i].push(divW.text());
            }
        }

        return {"answers": answers};
    }
  
    var resetSuper = self.reset;
    self.reset = () => {
        resetSuper();
    
        self.taskVariantInit(taskData);
    }
    
    self.taskVariantInit(taskData);
    return self;
}