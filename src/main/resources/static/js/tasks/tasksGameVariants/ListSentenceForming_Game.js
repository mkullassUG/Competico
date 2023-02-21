const ListSentenceForming_Game = (taskData) => {
    var self = TaskGameVariant(taskData);
    self.taskName = "ListSentenceForming";
    
    var taskVariantInitSuper = self.taskVariantInit;
    self.taskVariantInit = (taskData) => {
        taskVariantInitSuper(taskData);
    
        var sentence_rows = taskData.words;
        var DivListSentenceForming;
        var DivRandomizedWords;
        var DivOrderedWords;
        
        $("#GameDiv").html("");
        for ( let i = 0; i < sentence_rows.length; i++) {
            var row = sentence_rows[i];
            
            DivListSentenceForming = $(`<div class="Div` + self.taskName + `" id="Div` + self.taskName + `` + i + `">`);
            DivRandomizedWords = $(`<ul class="DivRandomizedWords border-primary connectedSortable`+i+`" id="DivRandomizedWords`+i+`">`);
            for (let j = 0; j < row.length; j++) {
                var word = document.createTextNode(row[j]);

                var li = $(`<li class="` + self.taskName + `word">`);
                li.append(word);
                li.dblclick(self.dbClick);
                DivRandomizedWords.append(li);
            }
    
            DivOrderedWords = $(`<ul class="DivOrderedWords border-bottom border-primary text-left pl-0 connectedSortable`+i+`" id="DivOrderedWords`+i+`">`);
            if ( sentence_rows.length > 1)
                DivListSentenceForming.append(`<div class="sentenceIndex">` + (i+1) + `</div>`);
            DivListSentenceForming.append(DivRandomizedWords);
            DivListSentenceForming.append(DivOrderedWords);
            
            $("#GameDiv").append(DivListSentenceForming);
            
            function doterHrUpdate ( e, ui ) {

                var elmnt = $(e.target).find("." + self.taskName + "word");

                if ( elmnt.length == 1 && $(e.target).hasClass("DivOrderedWords")) {
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
                cursor: "grabbing"
            }).disableSelection();
        }
    }
    
    self.dbClick = (e) => {

        var target = $(e.target);
        var parent = target.parent();
        if ( parent.hasClass("DivOrderedWords") ){
            parent.append($(target).remove());
            target.dblclick(self.dbClick);
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