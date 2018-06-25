package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu newMenu,
                      Errors errors) {
        if (errors.hasErrors()) {
            return "menu/add";
        } else {
            menuDao.save(newMenu);
            return "redirect:view/" + newMenu.getId();
        }
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute(menu);
        model.addAttribute("title", menu.getName());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("form", new AddMenuItemForm(menu, cheeseDao.findAll()));
        model.addAttribute("cheeses", cheeseDao.findAll());
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model, @PathVariable int id,
                          @RequestParam int cheeseId,
                          @ModelAttribute @Valid AddMenuItemForm newAddMenuItemForm,
                          Errors errors) {
        if (errors.hasErrors()) {
            Menu menu = menuDao.findOne(id);
            model.addAttribute("title","Add item to menu: " + menu.getName());
            model.addAttribute("form", new AddMenuItemForm(menu, cheeseDao.findAll()));
            return "menu/add-item";
        } else {
            Cheese newCheese = cheeseDao.findOne(cheeseId);
            Menu menu = menuDao.findOne(id);
            menu.addItem(newCheese);
            menuDao.save(menu);
            return "redirect:/menu/view/" + menu.getId();        }
    }

}
