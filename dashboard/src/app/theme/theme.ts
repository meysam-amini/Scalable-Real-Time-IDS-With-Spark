export interface Theme {
  name: string;
  properties: any;
}

export const light: Theme = {
  name: "light",
  properties: {
    "--foreground-default": "#424242",
    "--foreground-default-hover": "#000000",
    "--foreground-secondary": "#41474D",
    "--foreground-tertiary": "#797C80",
    "--foreground-quaternary": "#F4FAFF",

    "--background-default": "#f7f5f9",
    "--background-header":
      // "linear-gradient(0deg, rgba(255,255,255,1) 42%, rgba(252,255,209,0.8519607672170431) 100%, rgba(232,229,134,1) 100%)"
    " linear-gradient(0deg, rgba(254,254,254,1) 22%, rgba(247,233,202,1) 100%)"
    ,
    "--background-header-sideMenu": "#ffffff",
    "--background-sideMenu":
      "linear-gradient(180deg, rgba(255,255,255,1) 42%, rgba(252,255,209,0.8519607672170431) 100%, rgba(232,229,134,1) 100%)"
    ,
    "--background-logo": "#fffd9c",
    "--background-logo_hover": "#46494c",
    "--background-menu_items":
    "#ffdf7e"
    ,
    "--background-menu_items_hover":
      "linear-gradient(0deg,rgba(255,231,180,0.6418767336035978) 22%, rgba(254,254,254,1) 100%)"
    ,

    "--background-menu-items-click":
    "#ffffff"
    ,



    "--background-chart":
    "linear-gradient(180deg, rgba(255,255,255,1) 42%, rgba(252,255,209,0.8519607672170431) 100%, rgba(232,229,134,1) 100%)"
    ,
    "--border": "#636363",
    "--border-size": "1px",
    "--border-size-logo": "0.5px",



    "--primary-default": "#5DFDCB",
    "--primary-dark": "#24B286",
    "--primary-light": "#B2FFE7",

    "--error-default": "red",
    "--error-dark": "#800600",
    "--error-light": "#FFCECC",
    "--valid": "forestgreen",

    "--background-tertiary-shadow": "0 1px 3px 0 rgba(92, 125, 153, 0.5)",

    "--line-chart-icon":'url(assets/img/line_charts_dark.png)',
    "--radar-chart-icon":'url(assets/img/radar_chart_dark.png)',
    "--data-table-icon":'url(assets/img/data_table_dark.png)',
    "--api-setting-icon":'url(assets/img/api_setting_dark.png)',
    "--header-title-icon":'url(assets/img/header_title_dark.png)',
  }
};

export const dark: Theme = {
  name: "dark",
  properties: {
    "--foreground-default": "#dbdbdb",
    "--foreground-default-hover": "#808080",
    "--foreground-secondary": "#A3B9CC",
    "--foreground-tertiary": "#F4FAFF",
    "--foreground-quaternary": "#E5E5E5",


    "--background-default": "#3f3f3f",
    "--background-secondary": "#2d2b2e",
    "--background-header":
      'linear-gradient(0deg, rgba(0,0,0,1) 10%, rgba(0,36,61,1) 100%)'
    ,
    "--background-logo": "#2b2b2b",
    "--background-logo_hover": "#ffffff",
    "--background-menu_items":
    "#138496"
    ,
    "--background-menu_items_hover":
      "linear-gradient(0deg,rgba(255,231,180,0.6418767336035978) 22%, rgba(254,254,254,1) 100%)"
    ,

    "--background-menu-items-click":
      "#ffffff"
    ,

    "--background-header-sideMenu": "#181818",
    "--background-sideMenu":
      'linear-gradient(180deg, rgba(0,0,0,0.5) 10%, rgba(0,36,61,0.8) 100%)'
    ,
    "--background-chart":
      'linear-gradient(-180deg, rgba(0,0,0,0.5) 10%, rgba(0,36,61,0.8) 100%)'
    ,

    "--border": "#c1c1c1",
    "--border-size": "1px",
    "--border-size-logo": "0.5px",




    "--primary-default": "#5DFDCB",
    "--primary-dark": "#24B286",
    "--primary-light": "#B2FFE7",

    "--error-default": "#EF3E36",
    "--error-dark": "#800600",
    "--error-light": "#FFCECC",
    "--valid": "greenyellow",


    "--background-tertiary-shadow": "0 1px 3px 0 rgba(8, 9, 10, 0.5)",
    "--line-chart-icon":'url(assets/img/line_charts_light.png)',
    "--radar-chart-icon":'url(assets/img/radar_chart_light.png)',
    "--data-table-icon":'url(assets/img/data_table_light.png)',
    "--api-setting-icon":'url(assets/img/api_setting_light.png)',
    "--header-title-icon":'url(assets/img/header_title_light.png)',


  }
};
