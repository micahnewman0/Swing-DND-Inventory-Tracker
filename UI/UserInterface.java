package UI;

import Inven.Armor;
import Inven.Consumable;
import Inven.FullItemList;
import Inven.Inventory;
import Inven.Item;
import Inven.Weapon;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.function.IntSupplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;

public class UserInterface{
    private final FullItemList fullList;
    private final Inventory inventory;
    private final Runnable saveAll;
    private final IntSupplier newID;

    //Main//
    private final JFrame frame = new JFrame("Inventory Manager");
    private final InventoryTableModel invModel = new InventoryTableModel();
    private final JTable invTable = new JTable(invModel);
    private final JLabel totalLabel = new JLabel();

    public UserInterface(FullItemList fullList, Inventory inventory, 
        Runnable saveAll, IntSupplier newID){
            this.fullList = fullList;
            this.inventory = inventory;
            this.saveAll = saveAll;
            this.newID = newID;

            invTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            invTable.setFillsViewportHeight(true);

            JLabel title = new JLabel(inventory.getOwner() + "'s Inventory");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

            //Button//
            JButton addButton = new JButton("Add Item");
            JButton removeButton = new JButton("Remove Item");
            JButton changeButton = new JButton("Change Item");
            addButton.addActionListener(e -> showAddWindow());
            removeButton.addActionListener(e -> removeSelected());
            // NOT YET IMPLEMENTED: changeButton.addActionListener(e -> changeSelected());

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttons.add(addButton);
            buttons.add(removeButton);
            buttons.add(changeButton);

            JPanel bottom = new JPanel(new BorderLayout());
            bottom.add(buttons, BorderLayout.WEST);
            bottom.add(totalLabel, BorderLayout.EAST);
        
            JPanel panel = new JPanel(new BorderLayout(8,8));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            panel.add(title, BorderLayout.NORTH);
            panel.add(new JScrollPane(invTable), BorderLayout.CENTER);
            panel.add(bottom, BorderLayout.SOUTH);

            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800,500);
            frame.setLocationRelativeTo(null);
            refresh();
            frame.setVisible(true);
    }
    // Show the entire inventory again //
    // with relevant updates //
    private void refresh(){
        invModel.refresh();
        int total = 0;
        // For each item in Inventory set the key so we can iterate properly //
        for (int id : inventory.getContents().keySet()){
            Item item = fullList.findByID(id);
            if (item!= null) {
                //Get total value of item and quantity//
                total += item.getValue() * inventory.getQuantity(id);
            }
            totalLabel.setText("Total value: " + total);
        }
    }

    //Remove Item//
    private void removeSelected(){
        int row = invTable.getSelectedRow();
        if (row < 0){
            JOptionPane.showMessageDialog(frame, "Please select an item.");
            return;
        }
        int id = invModel.idAt(row);
        int held = inventory.getQuantity(id);
        
        JSpinner qty = new JSpinner(new SpinnerNumberModel(1,1,held,1));
        int answer = JOptionPane.showConfirmDialog(frame,
            new Object[]{"Quantity to remove? You have " + held + ":", qty},
            "Remove Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (answer == JOptionPane.OK_OPTION){
            inventory.removeItem(id, (Integer) qty.getValue());
            saveAll.run();
            refresh();
            }
        }

        //Shows the full list of items and allows//
        //for you to add from the full item list if desired//
        private void showAddWindow(){
            JDialog dialog = new JDialog(frame, "Add Item", true);

            JTable listTable = new JTable(new ItemListModel());
            listTable.setAutoCreateRowSorter(true);
            listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listTable.setFillsViewportHeight(true);

            JSpinner qty = new JSpinner(new SpinnerNumberModel(1,1,9999,1));
            JButton addbutton = new JButton("Add Selected to inventory");
            JButton newButton = new JButton("Create New Item...");
            JButton cancelButton = new JButton("Cancel");

            //Picking from the list//
            addbutton.addActionListener(e -> {
                int row = listTable.getSelectedRow();
                if (row < 0){
                    JOptionPane.showMessageDialog(dialog, "Select an item.");
                    return;
                }
                Item item = fullList.getItems().get(listTable.convertRowIndexToModel(row));
                inventory.addItem(item.getItemID(), (Integer) qty.getValue());
                saveAll.run();
                dialog.dispose();
                refresh();
            });

            //Making a brand new item!//
            newButton.addActionListener(e -> {
                if (createNewItem(dialog, (Integer) qty.getValue())){
                    dialog.dispose();
                    refresh();
                }
            });
            cancelButton.addActionListener(e -> dialog.dispose());
            
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.add(new JLabel("Quantity"));
            bottom.add(qty);
            bottom.add(addbutton);
            bottom.add(newButton);
            bottom.add(cancelButton);

            JPanel panel = new JPanel(new BorderLayout(8,8));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            panel.add(new JLabel("Choose an item from the full list or create a new one:"), BorderLayout.NORTH);
            panel.add(new JScrollPane(listTable), BorderLayout.CENTER);
            panel.add(bottom, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setSize(800,500);
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
    }

    //Adds a new item to the full list and the players inventory//
    private boolean createNewItem(Component parent, int defaultQty){
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Weapon", "Armor", "Consumable", "Misc"});
        JTextField name = new JTextField(20);
        JTextField value = new JTextField("0");
        JTextField desc = new JTextField();
        JTextField quantity = new JTextField(String.valueOf(defaultQty));
        //Weapon//
        JTextField dmg = new JTextField();
        JTextField hands = new JTextField("1");
        JTextField wpnType = new JTextField();
        JTextField hitMod = new JTextField("0");
        //Armor//
        JTextField ac = new JTextField("0");
        JTextField size = new JTextField();
        JTextField weight = new JTextField();
        //Consumable//
        JTextField uses = new JTextField("1");

        CardLayout cards = new CardLayout();
        JPanel details = new JPanel(cards);
        details.add(formPanel(new String[]{"Damage", "Hands", "Weapon Type", "Hit Modifier"},
            new JComponent[]{dmg,hands,wpnType,hitMod}), "Weapon");
        details.add(formPanel(new String[]{"AC","Size","Weight"},
            new JComponent[]{ac,size,weight}), "Armor");
        details.add(formPanel(new String[]{"Uses"}, 
            new JComponent[]{uses}), "Consumable");
        details.add(new JPanel(), "Misc");
        typeBox.addActionListener(e -> cards.show(details, (String)
        typeBox.getSelectedItem()));
        cards.show(details, (String) typeBox.getSelectedItem());

        JPanel form = new JPanel(new BorderLayout(0,10));
        form.add(formPanel(new String[]{"Type", "Name", "Value", "Description", "Quantity"},
            new JComponent[]{typeBox, name, value, desc, quantity}), BorderLayout.NORTH);
            form.add(details,BorderLayout.CENTER);

            while (true){
                int answer = JOptionPane.showConfirmDialog(parent, form, "Create New Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (answer != JOptionPane.OK_OPTION){
                    return false;
                }

                try {
                    String n = name.getText().trim();
                    if(n.isEmpty()) throw new IllegalArgumentException("Name is required");
                    int v = toInt(value.getText(),"Value");
                    int q = toInt(quantity.getText(), "Quantity");
                    if (q < 1) throw new IllegalArgumentException("Quantity must be at least 1.");
                    String d = desc.getText().trim();
                    String type = (String) typeBox.getSelectedItem();

                    int id = newID.getAsInt();
                    Item created;
                    switch(type){
                        case "Weapon":
                            created = new Weapon(id, n, v, d, "Weapon",
                            dmg.getText().trim(),
                            toInt(hands.getText(), "Hands"),
                            wpnType.getText().trim(),
                            toInt(hitMod.getText(), "Hit modifier"));
                        break;
                        case "Armor":
                            created = new Armor(id, n, v, d, "Armor",
                            toInt(ac.getText(), "AC"),
                            size.getText().trim(),
                            weight.getText().trim());
                        break;
                    case "Consumable":
                        created = new Consumable(id, n, v, d, "Consumable",
                            toInt(uses.getText(), "Uses"));
                        break;
                    default:
                        created = new Item(id, n, v, d, "Misc");
                    }

                    fullList.addItem(created);
                    inventory.addItem(id, q);
                    saveAll.run();
                    return true;
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(parent, e.getMessage(),
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
                }
        }
    }

    private JPanel formPanel(String[] labels, JComponent[] fields){
        JPanel panel = new JPanel(new GridLayout(0,2,8,6));
        for (int i = 0; i < labels.length; i++){
            panel.add(new JLabel(labels[i] + ":"));
            panel.add(fields[i]);
        }
        return panel;
    }

    private int toInt(String text, String label){
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }

    //Tables Models (AI Used entirely)//
    private class InventoryTableModel extends AbstractTableModel {
        private final String[] columns = {"Item", "Type", "Quantity", "Unit value", "Total value"};
        private ArrayList<Integer> ids = new ArrayList<>();

        public void refresh(){
            ids = new ArrayList<>(inventory.getContents().keySet());
            fireTableDataChanged();
        }

        public int idAt(int row){ return ids.get(row); }

        @Override public int getRowCount(){ return ids.size(); }
        @Override public int getColumnCount(){ return columns.length; }
        @Override public String getColumnName(int col){ return columns[col]; }

        @Override
        public Object getValueAt(int row, int col){
            int id = ids.get(row);
            int qty = inventory.getQuantity(id);
            Item item = fullList.findByID(id);
            switch (col){
                case 0: return (item != null) ? item.getName() : "[missing item " + id + "]";
                case 1: return (item != null) ? item.getType() : "";
                case 2: return qty;
                case 3: return (item != null) ? item.getValue() : 0;
                default: return (item != null) ? item.getValue() * qty : 0;
            }
        }
    }

    private class ItemListModel extends AbstractTableModel {
        private final String[] columns = {"Type", "Name", "Value", "Description"};

        @Override public int getRowCount(){ return fullList.getItems().size(); }
        @Override public int getColumnCount(){ return columns.length; }
        @Override public String getColumnName(int col){ return columns[col]; }

        @Override
        public Class<?> getColumnClass(int col){
            return (col == 2) ? Integer.class : String.class;
        }

        @Override
        public Object getValueAt(int row, int col){
            Item item = fullList.getItems().get(row);
            switch (col){
                case 0: return item.getType();
                case 1: return item.getName();
                case 2: return item.getValue();
                default: return item.getDesc();
            }
        }
    }
}
