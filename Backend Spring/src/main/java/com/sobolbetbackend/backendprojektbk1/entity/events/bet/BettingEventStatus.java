package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

public enum BettingEventStatus {
    DRAFT,       // редактируется лайнемейкером
    PUBLISHED,   // доступен игрокам
    CLOSED       // завершён, нельзя ставить
}
