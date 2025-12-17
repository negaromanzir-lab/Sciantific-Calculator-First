/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package scientificcalculatorwithcurrencyconverter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ScientificCalculatorWithCurrencyConverter extends Application {
    
    // Calculator Components
    private TextField display;
    private ListView<String> historyList;
    private List<String> calculationHistory;
    private String currentInput = "";
    private double result = 0;
    private String lastOperation = "";
    private boolean startNewNumber = true;
    private boolean errorState = false;
    private double memoryValue = 0;
    
    // Currency Converter Components
    private ComboBox<String> fromCurrencyComboBox;
    private ComboBox<String> toCurrencyComboBox;
    private TextField amountTextField;
    private TextField resultTextField;
    private TextArea exchangeRatesArea;
    private Label lastUpdateLabel;
    
    // Exchange Rates (as of latest data - these should be updated dynamically)
    private Map<String, Double> exchangeRates = new HashMap<>();
    private Map<String, String> currencyNames = new HashMap<>();
    private Map<String, String> currencySymbols = new HashMap<>();
    
    // Ethiopian currency
    private static final String ETB = "ETB";
    
    @Override
    public void start(Stage primaryStage) {
        calculationHistory = new ArrayList<>();
        initializeExchangeRates();
        
        // Create main layout with tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: #2c3e50;");
        
        // Create calculator tab
        Tab calculatorTab = new Tab("Scientific Calculator");
        calculatorTab.setContent(createCalculatorTab());
        calculatorTab.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
        
        // Create currency converter tab
        Tab converterTab = new Tab("Currency Converter");
        converterTab.setContent(createCurrencyConverterTab());
        converterTab.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
        
        tabPane.getTabs().addAll(calculatorTab, converterTab);
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // Create scene and stage
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Scientific Calculator + Ethiopian Currency Converter");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }
    
    private void initializeExchangeRates() {
        // Initialize currency names
        currencyNames.put("ETB", "Ethiopian Birr");
        currencyNames.put("USD", "US Dollar");
        currencyNames.put("EUR", "Euro");
        currencyNames.put("GBP", "British Pound");
        currencyNames.put("JPY", "Japanese Yen");
        currencyNames.put("CNY", "Chinese Yuan");
        currencyNames.put("AED", "UAE Dirham");
        currencyNames.put("SAR", "Saudi Riyal");
        currencyNames.put("KES", "Kenyan Shilling");
        currencyNames.put("SDG", "Sudanese Pound");
        currencyNames.put("EGP", "Egyptian Pound");
        currencyNames.put("INR", "Indian Rupee");
        currencyNames.put("CAD", "Canadian Dollar");
        currencyNames.put("AUD", "Australian Dollar");
        currencyNames.put("CHF", "Swiss Franc");
        
        // Initialize currency symbols
        currencySymbols.put("ETB", "Br");
        currencySymbols.put("USD", "$");
        currencySymbols.put("EUR", "€");
        currencySymbols.put("GBP", "£");
        currencySymbols.put("JPY", "¥");
        currencySymbols.put("CNY", "¥");
        currencySymbols.put("AED", "د.إ");
        currencySymbols.put("SAR", "ر.س");
        currencySymbols.put("KES", "KSh");
        currencySymbols.put("SDG", "ج.س");
        currencySymbols.put("EGP", "E£");
        currencySymbols.put("INR", "₹");
        currencySymbols.put("CAD", "C$");
        currencySymbols.put("AUD", "A$");
        currencySymbols.put("CHF", "CHF");
        
        // Initialize exchange rates (1 ETB to other currencies)
        // Note: These are approximate rates. In a real app, you would fetch these from an API
        exchangeRates.put("USD", 0.0175);  // 1 ETB = 0.0175 USD
        exchangeRates.put("EUR", 0.0160);  // 1 ETB = 0.0160 EUR
        exchangeRates.put("GBP", 0.0138);  // 1 ETB = 0.0138 GBP
        exchangeRates.put("JPY", 2.60);    // 1 ETB = 2.60 JPY
        exchangeRates.put("CNY", 0.126);   // 1 ETB = 0.126 CNY
        exchangeRates.put("AED", 0.064);   // 1 ETB = 0.064 AED
        exchangeRates.put("SAR", 0.066);   // 1 ETB = 0.066 SAR
        exchangeRates.put("KES", 2.50);    // 1 ETB = 2.50 KES
        exchangeRates.put("SDG", 10.0);    // 1 ETB = 10.0 SDG
        exchangeRates.put("EGP", 0.54);    // 1 ETB = 0.54 EGP
        exchangeRates.put("INR", 1.46);    // 1 ETB = 1.46 INR
        exchangeRates.put("CAD", 0.024);   // 1 ETB = 0.024 CAD
        exchangeRates.put("AUD", 0.027);   // 1 ETB = 0.027 AUD
        exchangeRates.put("CHF", 0.016);   // 1 ETB = 0.016 CHF
        exchangeRates.put("ETB", 1.0);     // 1 ETB = 1.0 ETB
    }
    
    private VBox createCalculatorTab() {
        // Create display
        display = new TextField("0");
        display.setEditable(false);
        display.setFont(Font.font("Monospace", FontWeight.BOLD, 28));
        display.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #00ff00; -fx-border-color: #00ff00; -fx-border-width: 2; -fx-border-radius: 5;");
        display.setPrefHeight(80);
        
        // Create button grid
        GridPane buttonGrid = createButtonGrid();

        
        // Create history panel
        VBox historyPanel = createHistoryPanel();
        
        // Create calculator content area
        VBox calculatorContent = new VBox(15);
        calculatorContent.getChildren().addAll(createCalculatorTitle(), display, buttonGrid);
        calculatorContent.setPadding(new Insets(10));
        
        // Create split pane for calculator and history
        SplitPane calculatorSplitPane = new SplitPane();
        calculatorSplitPane.getItems().addAll(calculatorContent, historyPanel);
        calculatorSplitPane.setDividerPositions(0.7);
        
        VBox calculatorTab = new VBox(calculatorSplitPane);
        calculatorTab.setPadding(new Insets(5));
        calculatorTab.setStyle("-fx-background-color: #2c3e50;");
        
        return calculatorTab;
    }
    
    private VBox createCurrencyConverterTab() {
        // Title
        Label title = new Label("Ethiopian Currency Converter");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        
        Label subtitle = new Label("Convert between Ethiopian Birr and other currencies");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.LIGHTGRAY);
        subtitle.setAlignment(Pos.CENTER);
        
        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        
        // Create converter panel
        GridPane converterGrid = createConverterGrid();
        
        // Create exchange rates display
        VBox ratesPanel = createExchangeRatesPanel();
        
        // Create main converter content
        VBox converterContent = new VBox(20);
        converterContent.getChildren().addAll(titleBox, converterGrid);
        converterContent.setPadding(new Insets(20));
        converterContent.setAlignment(Pos.TOP_CENTER);
        
        // Create split pane for converter and rates
        SplitPane converterSplitPane = new SplitPane();
        converterSplitPane.getItems().addAll(converterContent, ratesPanel);
        converterSplitPane.setDividerPositions(0.6);
        
        VBox converterTab = new VBox(converterSplitPane);
        converterTab.setPadding(new Insets(5));
        converterTab.setStyle("-fx-background-color: #2c3e50;");
        
        return converterTab;
    }
    
    private GridPane createConverterGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #34495e; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10;");
        grid.setAlignment(Pos.CENTER);
        
        // From currency
        Label fromLabel = new Label("From Currency:");
        fromLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        fromLabel.setTextFill(Color.WHITE);
        
        fromCurrencyComboBox = new ComboBox<>();
        ObservableList<String> currencies = FXCollections.observableArrayList(currencyNames.keySet());
        fromCurrencyComboBox.setItems(currencies);
        fromCurrencyComboBox.setValue("ETB");
        fromCurrencyComboBox.setPrefWidth(200);
        fromCurrencyComboBox.setStyle("-fx-font-size: 14; -fx-background-color: white;");
        
        // Amount
        Label amountLabel = new Label("Amount:");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        amountLabel.setTextFill(Color.WHITE);
        
        amountTextField = new TextField("1.00");
        amountTextField.setFont(Font.font("Arial", 14));
        amountTextField.setPrefWidth(200);
        amountTextField.setStyle("-fx-background-color: white;");
        
        // To currency
        Label toLabel = new Label("To Currency:");
        toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        toLabel.setTextFill(Color.WHITE);
        
        toCurrencyComboBox = new ComboBox<>();
        toCurrencyComboBox.setItems(currencies);
        toCurrencyComboBox.setValue("USD");
        toCurrencyComboBox.setPrefWidth(200);
        toCurrencyComboBox.setStyle("-fx-font-size: 14; -fx-background-color: white;");
        
        // Result
        Label resultLabel = new Label("Converted Amount:");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resultLabel.setTextFill(Color.WHITE);
        
        resultTextField = new TextField();
        resultTextField.setEditable(false);
        resultTextField.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        resultTextField.setPrefWidth(200);
        resultTextField.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #2ecc71; -fx-border-color: #2ecc71;");
        
        // Buttons
        Button convertButton = new Button("Convert");
        convertButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        convertButton.setPrefWidth(150);
        convertButton.setPrefHeight(40);
        convertButton.setOnAction(e -> convertCurrency());
        
        Button swapButton = new Button("Swap Currencies");
        swapButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        swapButton.setPrefWidth(150);
        swapButton.setPrefHeight(40);
        swapButton.setOnAction(e -> swapCurrencies());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        clearButton.setPrefWidth(150);
        clearButton.setPrefHeight(40);
        clearButton.setOnAction(e -> clearConverter());
        
        // Add hover effects
        for (Button btn : new Button[]{convertButton, swapButton, clearButton}) {
            btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.3), 10, 0, 0, 0);"));
            btn.setOnMouseExited(e -> {
                if (btn == convertButton) {
                    btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
                } else if (btn == swapButton) {
                    btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
                } else {
                    btn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
                }
            });
        }
        
        // Quick conversion buttons
        Label quickLabel = new Label("Quick Conversions:");
        quickLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        quickLabel.setTextFill(Color.WHITE);
        GridPane.setColumnSpan(quickLabel, 2);
        
        // Create quick conversion buttons
        HBox quickButtonsBox = new HBox(10);
        quickButtonsBox.setAlignment(Pos.CENTER);
        
        Button usdButton = createQuickConversionButton("USD", "$");
        Button eurButton = createQuickConversionButton("EUR", "€");
        Button gbpButton = createQuickConversionButton("GBP", "£");
        Button kesButton = createQuickConversionButton("KES", "KSh");
        
        quickButtonsBox.getChildren().addAll(usdButton, eurButton, gbpButton, kesButton);
        GridPane.setColumnSpan(quickButtonsBox, 2);
        
        // Add components to grid
        grid.add(fromLabel, 0, 0);
        grid.add(fromCurrencyComboBox, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountTextField, 1, 1);
        grid.add(toLabel, 0, 2);
        grid.add(toCurrencyComboBox, 1, 2);
        grid.add(resultLabel, 0, 3);
        grid.add(resultTextField, 1, 3);
        
        // Button row
        HBox buttonBox = new HBox(20, convertButton, swapButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);
        grid.add(buttonBox, 0, 4, 2, 1);
        
        // Quick conversions
        grid.add(quickLabel, 0, 5);
        grid.add(quickButtonsBox, 0, 6, 2, 1);
        
        // Add padding and spacing
        grid.setPadding(new Insets(30));
        
        // Perform initial conversion
        convertCurrency();
        
        return grid;
    }
    
    private Button createQuickConversionButton(String currency, String symbol) {
        Button button = new Button(symbol + " " + currency);
        button.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setPrefWidth(100);
        button.setOnAction(e -> {
            toCurrencyComboBox.setValue(currency);
            convertCurrency();
        });
        
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.3), 10, 0, 0, 0);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;"));
        
        return button;
    }
    
    private VBox createExchangeRatesPanel() {
        // Title
        Label ratesTitle = new Label("Exchange Rates (1 ETB =)");
        ratesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ratesTitle.setTextFill(Color.WHITE);
        ratesTitle.setAlignment(Pos.CENTER);
        
        // Last update label
        lastUpdateLabel = new Label("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        lastUpdateLabel.setFont(Font.font("Arial", 12));
        lastUpdateLabel.setTextFill(Color.LIGHTGRAY);
        lastUpdateLabel.setAlignment(Pos.CENTER);
        
        // Exchange rates display
        exchangeRatesArea = new TextArea();
        exchangeRatesArea.setEditable(false);
        exchangeRatesArea.setFont(Font.font("Monospace", 14));
        exchangeRatesArea.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #f39c12; -fx-border-color: #f39c12;");
        exchangeRatesArea.setPrefHeight(400);
        
        // Update rates button
        Button updateRatesButton = new Button("Update Rates");
        updateRatesButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        updateRatesButton.setPrefWidth(150);
        updateRatesButton.setOnAction(e -> updateExchangeRates());
        
        // Add hover effect
        updateRatesButton.setOnMouseEntered(e -> updateRatesButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.3), 10, 0, 0, 0);"));
        updateRatesButton.setOnMouseExited(e -> updateRatesButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;"));
        
        // Update the rates display
        updateExchangeRatesDisplay();
        
        VBox ratesPanel = new VBox(15, ratesTitle, lastUpdateLabel, exchangeRatesArea, updateRatesButton);
        ratesPanel.setPadding(new Insets(20));
        ratesPanel.setAlignment(Pos.TOP_CENTER);
        ratesPanel.setStyle("-fx-background-color: #34495e; -fx-border-color: #f39c12; -fx-border-width: 2; -fx-border-radius: 10;");
        
        return ratesPanel;
    }
    
    private void updateExchangeRatesDisplay() {
        StringBuilder ratesText = new StringBuilder();
        ratesText.append("Currency\t\tRate\n");
        ratesText.append("--------\t\t----\n");
        
        // Sort currencies for better display
        List<String> sortedCurrencies = new ArrayList<>(exchangeRates.keySet());
        Collections.sort(sortedCurrencies);
        
        for (String currency : sortedCurrencies) {
            if (!currency.equals("ETB")) {
                double rate = exchangeRates.get(currency);
                String currencyName = currencyNames.get(currency);
                String symbol = currencySymbols.get(currency);
                
                if (rate >= 1) {
                    ratesText.append(String.format("%s (%s)\t\t%.4f\n", 
                        currencyName, currency, rate));
                } else {
                    ratesText.append(String.format("%s (%s)\t\t%.6f\n", 
                        currencyName, currency, rate));
                }
            }
        }
        
        exchangeRatesArea.setText(ratesText.toString());
    }
    
    private void convertCurrency() {
        try {
            String fromCurrency = fromCurrencyComboBox.getValue();
            String toCurrency = toCurrencyComboBox.getValue();
            
            double amount = Double.parseDouble(amountTextField.getText());
            
            // Get conversion rate
            double rate = getConversionRate(fromCurrency, toCurrency);
            double convertedAmount = amount * rate;
            
            // Format the result
            String fromSymbol = currencySymbols.getOrDefault(fromCurrency, "");
            String toSymbol = currencySymbols.getOrDefault(toCurrency, "");
            
            String resultText = String.format("%s%.2f %s = %s%.2f %s",
                fromSymbol, amount, fromCurrency,
                toSymbol, convertedAmount, toCurrency);
            
            resultTextField.setText(resultText);
            
            // Add to history
            String historyEntry = String.format("[%s] %s %.2f %s → %s %.2f %s (Rate: %.6f)",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                fromSymbol, amount, fromCurrency,
                toSymbol, convertedAmount, toCurrency,
                rate);
            
            addConversionHistory(historyEntry);
            
        } catch (NumberFormatException e) {
            resultTextField.setText("Error: Invalid amount");
        } catch (Exception e) {
            resultTextField.setText("Error: Conversion failed");
        }
    }
    
    private double getConversionRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }
        
        // Convert fromCurrency to ETB first
        double fromRate = exchangeRates.getOrDefault(fromCurrency, 1.0);
        double toRate = exchangeRates.getOrDefault(toCurrency, 1.0);
        
        // If fromCurrency is ETB, use toRate directly
        if (fromCurrency.equals("ETB")) {
            return toRate;
        }
        
        // If toCurrency is ETB, use 1/fromRate
        if (toCurrency.equals("ETB")) {
            return 1.0 / fromRate;
        }
        
        // Convert through ETB: fromCurrency → ETB → toCurrency
        double inETB = 1.0 / fromRate;
        return inETB * toRate;
    }
    
    private void swapCurrencies() {
        String fromCurrency = fromCurrencyComboBox.getValue();
        String toCurrency = toCurrencyComboBox.getValue();
        
        fromCurrencyComboBox.setValue(toCurrency);
        toCurrencyComboBox.setValue(fromCurrency);
        
        convertCurrency();
    }
    
    private void clearConverter() {
        amountTextField.setText("1.00");
        resultTextField.clear();
        fromCurrencyComboBox.setValue("ETB");
        toCurrencyComboBox.setValue("USD");
        convertCurrency();
    }
    
    private void updateExchangeRates() {
        // In a real application, this would fetch rates from an API
        // For now, we'll simulate an update with minor random changes
        
        Random random = new Random();
        for (String currency : exchangeRates.keySet()) {
            if (!currency.equals("ETB")) {
                double currentRate = exchangeRates.get(currency);
                // Add small random change (±2%)
                double change = 1 + (random.nextDouble() * 0.04 - 0.02);
                exchangeRates.put(currency, currentRate * change);
            }
        }
        
        // Update display
        updateExchangeRatesDisplay();
        lastUpdateLabel.setText("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Show confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rates Updated");
        alert.setHeaderText(null);
        alert.setContentText("Exchange rates have been updated with simulated changes.");
        alert.showAndWait();
        
        // Reconvert with new rates
        convertCurrency();
    }
    
    private void addConversionHistory(String entry) {
        calculationHistory.add(0, entry);
        historyList.getItems().add(0, entry);
        
        // Limit history to 50 entries
        if (calculationHistory.size() > 50) {
            calculationHistory.remove(calculationHistory.size() - 1);
            historyList.getItems().remove(historyList.getItems().size() - 1);
        }
    }
    
    // The rest of the calculator methods remain the same as before...
    // Only showing the currency converter related code above
    
    // [Previous calculator methods would go here - they remain unchanged]
    // For brevity, I'm including only the currency converter specific methods
    // The calculator methods from the previous version should be copied here
    
    private VBox createHistoryPanel() {
        // ... [Same as previous version] ...
        Label historyTitle = new Label("Calculation History");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        historyTitle.setTextFill(Color.WHITE);
        historyTitle.setAlignment(Pos.CENTER);
        
        historyList = new ListView<>();
        historyList.setPrefHeight(500);
        historyList.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5;");
        historyList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #2c3e50;");
                } else {
                    setText(item);
                    setFont(Font.font("Monospace", 12));
                    if (item.contains("→")) {
                        // Currency conversion entries
                        setTextFill(Color.LIGHTBLUE);
                    } else if (item.contains("Error")) {
                        // Error entries
                        setTextFill(Color.LIGHTCORAL);
                    } else {
                        // Regular calculation entries
                        setTextFill(Color.LIGHTGRAY);
                    }
                    setStyle("-fx-background-color: #34495e; -fx-border-color: #4a6572; -fx-border-width: 0 0 1 0;");
                }
            }
        });
        
        // History buttons
        Button clearHistoryBtn = new Button("Clear History");
        clearHistoryBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        clearHistoryBtn.setPrefWidth(150);
        clearHistoryBtn.setOnAction(e -> clearHistory());
        
        Button copyHistoryBtn = new Button("Copy Selected");
        copyHistoryBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        copyHistoryBtn.setPrefWidth(150);
        copyHistoryBtn.setOnAction(e -> copySelectedHistory());
        
        Button insertHistoryBtn = new Button("Insert to Display");
        insertHistoryBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        insertHistoryBtn.setPrefWidth(150);
        insertHistoryBtn.setOnAction(e -> insertFromHistory());
        
        // Double click to insert from history
        historyList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                insertFromHistory();
            }
        });
        
        HBox buttonBox = new HBox(10, clearHistoryBtn, copyHistoryBtn, insertHistoryBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        VBox historyContainer = new VBox(10, historyTitle, historyList, buttonBox);
        historyContainer.setPadding(new Insets(10));
        historyContainer.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10;");
        historyContainer.setPrefWidth(250);
        
        return historyContainer;
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #2c3e50;");
        
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem clearHistoryItem = new MenuItem("Clear History");
        clearHistoryItem.setOnAction(e -> clearHistory());
        MenuItem exportHistoryItem = new MenuItem("Export History...");
        exportHistoryItem.setOnAction(e -> exportHistory());
        MenuItem updateRatesItem = new MenuItem("Update Exchange Rates");
        updateRatesItem.setOnAction(e -> updateExchangeRates());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(clearHistoryItem, new SeparatorMenuItem(), 
                                  exportHistoryItem, updateRatesItem, new SeparatorMenuItem(), exitItem);
        
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem copyItem = new MenuItem("Copy Display");
        copyItem.setOnAction(e -> copyDisplay());
        MenuItem pasteItem = new MenuItem("Paste to Display");
        pasteItem.setOnAction(e -> pasteToDisplay());
        MenuItem clearItem = new MenuItem("Clear Calculator");
        clearItem.setOnAction(e -> clearAll());
        editMenu.getItems().addAll(copyItem, pasteItem, new SeparatorMenuItem(), clearItem);
        
        // View Menu
        Menu viewMenu = new Menu("View");
        CheckMenuItem showHistoryItem = new CheckMenuItem("Show History Panel");
        showHistoryItem.setSelected(true);
        CheckMenuItem scientificModeItem = new CheckMenuItem("Scientific Mode");
        scientificModeItem.setSelected(true);
        MenuItem themeItem = new MenuItem("Change Theme...");
        viewMenu.getItems().addAll(showHistoryItem, scientificModeItem, new SeparatorMenuItem(), themeItem);
        
        // Tools Menu (New)
        Menu toolsMenu = new Menu("Tools");
        MenuItem currencyConverterItem = new MenuItem("Currency Converter");
        currencyConverterItem.setOnAction(e -> switchToConverterTab());
        MenuItem calculatorItem = new MenuItem("Scientific Calculator");
        calculatorItem.setOnAction(e -> switchToCalculatorTab());
        toolsMenu.getItems().addAll(currencyConverterItem, calculatorItem);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        MenuItem manualItem = new MenuItem("User Manual");
        MenuItem ratesInfoItem = new MenuItem("Exchange Rates Info");
        ratesInfoItem.setOnAction(e -> showRatesInfo());
        helpMenu.getItems().addAll(aboutItem, manualItem, ratesInfoItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, helpMenu);
        return menuBar;
    }
    
    private void switchToConverterTab() {
        TabPane tabPane = (TabPane) ((BorderPane) historyList.getScene().getRoot()).getCenter();
        tabPane.getSelectionModel().select(1); // Select converter tab (index 1)
    }
    
    private void switchToCalculatorTab() {
        TabPane tabPane = (TabPane) ((BorderPane) historyList.getScene().getRoot()).getCenter();
        tabPane.getSelectionModel().select(0); // Select calculator tab (index 0)
    }
    
    private void showRatesInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exchange Rates Information");
        alert.setHeaderText("About Exchange Rates");
        alert.setContentText("Current exchange rates are simulated for demonstration purposes.\n\n" +
                           "In a production application, rates would be fetched from:\n" +
                           "• National Bank of Ethiopia API\n" +
                           "• European Central Bank\n" +
                           "• Open Exchange Rates API\n" +
                           "• Other financial data providers\n\n" +
                           "Rates are updated periodically and represent mid-market rates.");
        alert.setWidth(500);
        alert.showAndWait();
    }
    
    private void clearHistory() {
        calculationHistory.clear();
        historyList.getItems().clear();
        addConversionHistory("[History Cleared]");
    }
    
    private void copySelectedHistory() {
        String selected = historyList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Copy to clipboard
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(selected);
            javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Copy Successful");
            alert.setHeaderText(null);
            alert.setContentText("Entry copied to clipboard!");
            alert.showAndWait();
        }
    }
    
    private void insertFromHistory() {
        String selected = historyList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Extract numeric value from history entry
            String[] parts = selected.split("=");
            if (parts.length > 1) {
                String resultPart = parts[1].trim();
                // Extract first number from result
                String[] numbers = resultPart.split(" ");
                for (String num : numbers) {
                    if (num.matches("-?\\d+(\\.\\d+)?")) {
                        currentInput = num;
                        display.setText(currentInput);
                        startNewNumber = false;
                        break;
                    }
                }
            }
        }
    }
    
    private void exportHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export History");
        alert.setHeaderText("Feature not implemented");
        alert.setContentText("This feature would save history to a file.");
        alert.showAndWait();
    }
    
    private void copyDisplay() {
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(display.getText());
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
    }
    
    private void pasteToDisplay() {
        String clipboardText = javafx.scene.input.Clipboard.getSystemClipboard().getString();
        if (clipboardText != null && clipboardText.matches("[0-9.\\-+Ee]+")) {
            currentInput = clipboardText;
            display.setText(currentInput);
            startNewNumber = false;
        }
    }
    
    private VBox createCalculatorTitle() {
        Label title = new Label("SCIENTIFIC CALCULATOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        
        Label subtitle = new Label("with History Panel");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.LIGHTGRAY);
        subtitle.setAlignment(Pos.CENTER);
        
        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 10, 0));
        
        return titleBox;
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Scientific Calculator + Ethiopian Currency Converter");
        alert.setContentText("Version 3.0\n\nFeatures:\n• Scientific calculator functions\n• Calculation history with timestamps\n• Ethiopian currency converter\n• Multiple currency support\n• Real-time exchange rates (simulated)\n• Modern JavaFX interface\n\nCreated with JavaFX");
        alert.showAndWait();
    }
    
    // Note: The rest of the calculator button handling methods would be here
    // They are the same as in the previous version, so for brevity they're not repeated
    
    public static void main(String[] args) {
        launch(args);
    }

    private void clearAll() {
        currentInput = "";
        result = 0;
        lastOperation = "";
        startNewNumber = true;
        errorState = false;
        display.setText("0");
    }
    
    private GridPane createButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);
        
        // Button labels
        String[][] buttonLabels = {
            {"sin", "cos", "tan", "log", "ln"},
            {"√", "x²", "xʸ", "1/x", "π"},
            {"7", "8", "9", "÷", "C"},
            {"4", "5", "6", "×", "CE"},
            {"1", "2", "3", "-", "MC"},
            {"0", ".", "=", "+", "MR"}
        };
        
        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                Button button = createButton(buttonLabels[row][col]);
                grid.add(button, col, row);
            }
        }
        
        return grid;
    }
    
    private Button createButton(String label) {
        Button button = new Button(label);
        button.setPrefSize(80, 60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Style based on button type
        if (label.matches("[0-9.]")) {
            // Number buttons
            button.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-border-width: 1;");
        } else if (label.matches("[+\\-×÷=]")) {
            // Operation buttons
            button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-color: #c0392b; -fx-border-width: 1;");
        } else if (label.equals("C") || label.equals("CE")) {
            // Clear buttons
            button.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-color: #7f8c8d; -fx-border-width: 1;");
        } else {
            // Function buttons
            button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-color: #2980b9; -fx-border-width: 1;");
        }
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.3), 10, 0, 0, 0);"));
        button.setOnMouseExited(e -> {
            if (label.matches("[0-9.]")) {
                button.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-border-width: 1;");
            } else if (label.matches("[+\\-×÷=]")) {
                button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-color: #c0392b; -fx-border-width: 1;");
            } else if (label.equals("C") || label.equals("CE")) {
                button.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-border-color: #7f8c8d; -fx-border-width: 1;");
            } else {
                button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-color: #2980b9; -fx-border-width: 1;");
            }
        });
        
        button.setOnAction(e -> handleButtonClick(label));
        
        return button;
    }
    
    private void handleButtonClick(String label) {
        if (errorState && !label.equals("C")) {
            return;
        }
        
        try {
            switch (label) {
                case "0": case "1": case "2": case "3": case "4":
                case "5": case "6": case "7": case "8": case "9":
                    handleNumberInput(label);
                    break;
                case ".":
                    handleDecimalPoint();
                    break;
                case "+": case "-": case "×": case "÷":
                    handleOperation(label);
                    break;
                case "=":
                    handleEquals();
                    break;
                case "C":
                    clearAll();
                    break;
                case "CE":
                    clearEntry();
                    break;
                case "sin":
                    handleTrigFunction("sin");
                    break;
                case "cos":
                    handleTrigFunction("cos");
                    break;
                case "tan":
                    handleTrigFunction("tan");
                    break;
                case "log":
                    handleLogFunction("log");
                    break;
                case "ln":
                    handleLogFunction("ln");
                    break;
                case "√":
                    handleSquareRoot();
                    break;
                case "x²":
                    handleSquare();
                    break;
                case "xʸ":
                    handleOperation("^");
                    break;
                case "1/x":
                    handleReciprocal();
                    break;
                case "π":
                    handlePi();
                    break;
                case "MC":
                    memoryValue = 0;
                    break;
                case "MR":
                    currentInput = String.valueOf(memoryValue);
                    display.setText(currentInput);
                    startNewNumber = false;
                    break;
            }
        } catch (Exception e) {
            displayError("Error");
        }
    }
    
    private void handleNumberInput(String number) {
        if (startNewNumber) {
            currentInput = number;
            startNewNumber = false;
        } else {
            currentInput += number;
        }
        display.setText(currentInput);
    }
    
    private void handleDecimalPoint() {
        if (startNewNumber) {
            currentInput = "0.";
            startNewNumber = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        display.setText(currentInput);
    }
    
    private void handleOperation(String operation) {
        if (!currentInput.isEmpty()) {
            if (!lastOperation.isEmpty()) {
                handleEquals();
            } else {
                result = Double.parseDouble(currentInput);
            }
        }
        lastOperation = operation;
        startNewNumber = true;
    }
    
    private void handleEquals() {
        if (currentInput.isEmpty() || lastOperation.isEmpty()) {
            return;
        }
        
        double currentValue = Double.parseDouble(currentInput);
        double previousResult = result;
        String operation = lastOperation;
        
        switch (lastOperation) {
            case "+":
                result += currentValue;
                break;
            case "-":
                result -= currentValue;
                break;
            case "×":
                result *= currentValue;
                break;
            case "÷":
                if (currentValue == 0) {
                    displayError("Cannot divide by zero");
                    return;
                }
                result /= currentValue;
                break;
            case "^":
                result = Math.pow(result, currentValue);
                break;
        }
        
        String historyEntry = String.format("[%s] %.4f %s %.4f = %.4f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            previousResult, operation, currentValue, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        lastOperation = "";
        startNewNumber = true;
    }
    
    private void handleTrigFunction(String function) {
        if (currentInput.isEmpty()) return;
        
        double value = Double.parseDouble(currentInput);
        double radians = Math.toRadians(value);
        
        switch (function) {
            case "sin":
                result = Math.sin(radians);
                break;
            case "cos":
                result = Math.cos(radians);
                break;
            case "tan":
                result = Math.tan(radians);
                break;
        }
        
        String historyEntry = String.format("[%s] %s(%.4f°) = %.8f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            function, value, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        startNewNumber = true;
    }
    
    private void handleLogFunction(String function) {
        if (currentInput.isEmpty()) return;
        
        double value = Double.parseDouble(currentInput);
        
        if (value <= 0) {
            displayError("Invalid input for log");
            return;
        }
        
        if (function.equals("log")) {
            result = Math.log10(value);
        } else {
            result = Math.log(value);
        }
        
        String historyEntry = String.format("[%s] %s(%.4f) = %.8f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            function, value, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        startNewNumber = true;
    }
    
    private void handleSquareRoot() {
        if (currentInput.isEmpty()) return;
        
        double value = Double.parseDouble(currentInput);
        
        if (value < 0) {
            displayError("Cannot calculate square root of negative number");
            return;
        }
        
        result = Math.sqrt(value);
        
        String historyEntry = String.format("[%s] √%.4f = %.8f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            value, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        startNewNumber = true;
    }
    
    private void handleSquare() {
        if (currentInput.isEmpty()) return;
        
        double value = Double.parseDouble(currentInput);
        result = value * value;
        
        String historyEntry = String.format("[%s] %.4f² = %.8f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            value, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        startNewNumber = true;
    }
    
    private void handleReciprocal() {
        if (currentInput.isEmpty()) return;
        
        double value = Double.parseDouble(currentInput);
        
        if (value == 0) {
            displayError("Cannot divide by zero");
            return;
        }
        
        result = 1.0 / value;
        
        String historyEntry = String.format("[%s] 1/%.4f = %.8f",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            value, result);
        
        addToHistory(historyEntry);
        
        currentInput = String.valueOf(result);
        display.setText(String.format("%.8f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
        startNewNumber = true;
    }
    
    private void handlePi() {
        currentInput = String.valueOf(Math.PI);
        display.setText(String.format("%.8f", Math.PI));
        startNewNumber = false;
    }
    
    private void clearEntry() {
        currentInput = "";
        display.setText("0");
        startNewNumber = true;
    }
    
    private void displayError(String message) {
        display.setText(message);
        errorState = true;
        
        String historyEntry = String.format("[%s] Error: %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            message);
        
        addToHistory(historyEntry);
        
        // Reset after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    clearAll();
                    errorState = false;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void addToHistory(String entry) {
        calculationHistory.add(0, entry);
        historyList.getItems().add(0, entry);
        
        if (calculationHistory.size() > 50) {
            calculationHistory.remove(calculationHistory.size() - 1);
            historyList.getItems().remove(historyList.getItems().size() - 1);
        }
    }
}