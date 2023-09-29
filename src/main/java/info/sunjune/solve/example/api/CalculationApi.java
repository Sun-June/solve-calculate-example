package info.sunjune.solve.example.api;

import cn.hutool.json.JSONUtil;
import info.sunjune.solve.calculation.calculator.MixedCalculator;
import info.sunjune.solve.calculation.calculator.NumberCalculator;
import info.sunjune.solve.calculation.calculator.context.MixedContext;
import info.sunjune.solve.calculation.calculator.context.NumberContext;
import info.sunjune.solve.calculation.define.Context;
import info.sunjune.solve.calculation.error.CalculationException;
import info.sunjune.solve.calculation.error.FormulaException;
import info.sunjune.solve.calculation.util.BothValue;
import info.sunjune.solve.calculation.util.ValueUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CalculationApi {

    NumberCalculator calculator = new NumberCalculator();

    MixedCalculator mixedCalculator = new MixedCalculator();

    @RequestMapping(value = "/number/calculation", method = RequestMethod.POST)
    Map<String, Object> numberCalculation(@RequestBody Map<String, Object> data) {
        BothValue<Number, Context<Number>> both = null;
        try {
            both = calculator.calculationBoth(data.remove("formula").toString(), new NumberContext() {
                @Override
                public Number getCustomerLiteralValue(String literal) {
                    var number = super.getCustomerLiteralValue(literal);
                    if (number == null && data.containsKey(literal)) {
                        return ValueUtil.getNumberByString(data.get(literal).toString());
                    }
                    return number;
                }
            });
        } catch (FormulaException e) {
            return Map.of("success", false, "errorType", "formula", "error", e.error.name(),
                    "startIndex", e.startIndex, "endIndex", e.endIndex, "itemKind", e.item.kind);
        } catch (CalculationException e) {
            return Map.of("success", false, "errorType", "calculation", "error", e.getErrorInfo().name(),
                    "startIndex", e.context.pendingItem.startIndex, "errorStr", e.context.pendingItem.source,
                    "itemKind", e.context.pendingItem.kind);
        }
        return Map.of("success", true, "result", both.getLeft(), "recordList", both.getRight().recordList);
    }


    @RequestMapping(value = "/mixed/calculation", method = RequestMethod.POST)
    Map<String, Object> mixedCalculation(@RequestBody Map<String, Object> data) {
        BothValue<Object, Context<Object>> both = null;
        try {
            both = mixedCalculator.calculationBoth(data.remove("formula").toString(), new MixedContext() {
                @Override
                public Object getCustomerLiteralValue(String literal) {
                    var value = super.getCustomerLiteralValue(literal);
                    if (value == null && data.containsKey(literal) && literal.contains("number")) {
                        value = ValueUtil.getNumberByString(data.get(literal).toString());
                    }
                    if (value == null) {
                        return data.get(literal);
                    }
                    return value;
                }
            });
        } catch (FormulaException e) {
            return Map.of("success", false, "errorType", "formula", "error", e.error.name(),
                    "startIndex", e.startIndex, "endIndex", e.endIndex, "itemKind", e.item.kind);
        } catch (CalculationException e) {
            return Map.of("success", false, "errorType", "calculation", "error", e.getErrorInfo().name(),
                    "startIndex", e.context.pendingItem.startIndex, "errorStr", e.context.pendingItem.source,
                    "itemKind", e.context.pendingItem.kind);
        }
        return Map.of("success", true, "result", both.getLeft(), "recordList", both.getRight().recordList);
    }

    @RequestMapping(value = "/mixed/check", method = RequestMethod.POST)
    Map<String, Object> mixedFormulaCheck(@RequestBody Map<String, Object> data) {
        try {
            mixedCalculator.checkFormula(data.remove("formula").toString(), new MixedContext() {
                @Override
                public Object getCustomerLiteralValue(String literal) {
                    var value = super.getCustomerLiteralValue(literal);
                    if (value == null && data.containsKey(literal) && literal.contains("number")) {
                        value = ValueUtil.getNumberByString(data.get(literal).toString());
                    }
                    if (value == null) {
                        return data.get(literal);
                    }
                    return value;
                }
            });
        } catch (FormulaException e) {
            return Map.of("success", false, "errorType", "formula", "error", e.error.name(),
                    "startIndex", e.startIndex, "endIndex", e.endIndex, "itemKind", e.item.kind);
        }
        return Map.of("success", true);
    }

}
