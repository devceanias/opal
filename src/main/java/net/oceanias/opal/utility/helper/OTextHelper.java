package net.oceanias.opal.utility.helper;

@SuppressWarnings("unused")
public final class OTextHelper {
    public static final String COLOUR_CODE_REGEX = "&[0-9a-fk-or]";

    public static final String CHAT_DIVIDER_SHORT =
        "&#AAAAAA&m &#ABABAB&m &#ADADAD&m &#AEAEAE&m &#B0B0B0&m &#B1B1B1&m &#B3B3B3&m &#B4B4B4&m &#B6B6B6&m &#B7B7B7&m &#B8B8B8&m &#BABABA&m &#BBBBBB&m &#BDBDBD&m &#BEBEBE&m &#C0C0C0&m &#C1C1C1&m &#C2C2C2&m &#C4C4C4&m &#C5C5C5&m &#C7C7C7&m &#C8C8C8&m &#CACACA&m &#CBCBCB&m &#CDCDCD&m &#CECECE&m &#CFCFCF&m &#D1D1D1&m &#D2D2D2&m &#D4D4D4&m &#D5D5D5&m &#D7D7D7&m &#D8D8D8&m &#DADADA&m &#DBDBDB&m &#DCDCDC&m &#DEDEDE&m &#DFDFDF&m &#E1E1E1&m &#E2E2E2&m &#E4E4E4&m &#E5E5E5&m &#E7E7E7&m &#E8E8E8&m &#E9E9E9&m &#EBEBEB&m &#ECECEC&m &#EEEEEE&m &#EFEFEF&m &#F1F1F1&m &#F2F2F2&m &#F3F3F3&m &#F5F5F5&m &#F6F6F6&m &#F8F8F8&m &#F9F9F9&m &#FBFBFB&m &#FCFCFC&m &#FEFEFE&m &#FFFFFF&m &r";

    public static final String CHAT_DIVIDER_LONG =
        "&#AAAAAA&m &#ABABAB&m &#ACACAC&m &#ADADAD&m &#AFAFAF&m &#B0B0B0&m &#B1B1B1&m &#B2B2B2&m &#B3B3B3&m &#B4B4B4&m &#B6B6B6&m &#B7B7B7&m &#B8B8B8&m &#B9B9B9&m &#BABABA&m &#BBBBBB&m &#BDBDBD&m &#BEBEBE&m &#BFBFBF&m &#C0C0C0&m &#C1C1C1&m &#C2C2C2&m &#C4C4C4&m &#C5C5C5&m &#C6C6C6&m &#C7C7C7&m &#C8C8C8&m &#C9C9C9&m &#CBCBCB&m &#CCCCCC&m &#CDCDCD&m &#CECECE&m &#CFCFCF&m &#D0D0D0&m &#D2D2D2&m &#D3D3D3&m &#D4D4D4&m &#D5D5D5&m &#D6D6D6&m &#D7D7D7&m &#D9D9D9&m &#DADADA&m &#DBDBDB&m &#DCDCDC&m &#DDDDDD&m &#DEDEDE&m &#E0E0E0&m &#E1E1E1&m &#E2E2E2&m &#E3E3E3&m &#E4E4E4&m &#E5E5E5&m &#E7E7E7&m &#E8E8E8&m &#E9E9E9&m &#EAEAEA&m &#EBEBEB&m &#ECECEC&m &#EEEEEE&m &#EFEFEF&m &#F0F0F0&m &#F1F1F1&m &#F2F2F2&m &#F3F3F3&m &#F5F5F5&m &#F6F6F6&m &#F7F7F7&m &#F8F8F8&m &#F9F9F9&m &#FAFAFA&m &#FCFCFC&m &#FDFDFD&m &#FEFEFE&m &#FFFFFF&m &r";

    public static final String LORE_DIVIDER_SHORT =
        "&#555555&m &#575757&m &#595959&m &#5B5B5B&m &#5D5D5D&m &#5E5E5E&m &#606060&m &#626262&m &#646464&m &#666666&m &#686868&m &#6A6A6A&m &#6C6C6C&m &#6E6E6E&m &#6F6F6F&m &#717171&m &#737373&m &#757575&m &#777777&m &#797979&m &#7B7B7B&m &#7D7D7D&m &#7F7F7F&m &#808080&m &#828282&m &#848484&m &#868686&m &#888888&m &#8A8A8A&m &#8C8C8C&m &#8E8E8E&m &#909090&m &#919191&m &#939393&m &#959595&m &#979797&m &#999999&m &#9B9B9B&m &#9D9D9D&m &#9F9F9F&m &#A1A1A1&m &#A2A2A2&m &#A4A4A4&m &#A6A6A6&m &#A8A8A8&m &#AAAAAA&m &r";

    public static final String LORE_DIVIDER_LONG =
        "&#555555&m &#565656&m &#585858&m &#595959&m &#5B5B5B&m &#5C5C5C&m &#5E5E5E&m &#5F5F5F&m &#616161&m &#626262&m &#636363&m &#656565&m &#666666&m &#686868&m &#696969&m &#6B6B6B&m &#6C6C6C&m &#6D6D6D&m &#6F6F6F&m &#707070&m &#727272&m &#737373&m &#757575&m &#767676&m &#787878&m &#797979&m &#7A7A7A&m &#7C7C7C&m &#7D7D7D&m &#7F7F7F&m &#808080&m &#828282&m &#838383&m &#858585&m &#868686&m &#878787&m &#898989&m &#8A8A8A&m &#8C8C8C&m &#8D8D8D&m &#8F8F8F&m &#909090&m &#929292&m &#939393&m &#949494&m &#969696&m &#979797&m &#999999&m &#9A9A9A&m &#9C9C9C&m &#9D9D9D&m &#9E9E9E&m &#A0A0A0&m &#A1A1A1&m &#A3A3A3&m &#A4A4A4&m &#A6A6A6&m &#A7A7A7&m &#A9A9A9&m &#AAAAAA&m &r";

    public static final String SIDEBAR_DIVIDER_MAIN =
        "&#AAAAAA&m &#ADADAD&m &#B0B0B0&m &#B3B3B3&m &#B6B6B6&m &#B9B9B9&m &#BCBCBC&m &#BFBFBF&m &#C1C1C1&m &#C4C4C4&m &#C7C7C7&m &#CACACA&m &#CDCDCD&m &#D0D0D0&m &#D3D3D3&m &#D6D6D6&m &#D9D9D9&m &#DCDCDC&m &#DFDFDF&m &#E2E2E2&m &#E5E5E5&m &#E8E8E8&m &#EAEAEA&m &#EDEDED&m &#F0F0F0&m &#F3F3F3&m &#F6F6F6&m &#F9F9F9&m &#FCFCFC&m &#FFFFFF&m &r";
}