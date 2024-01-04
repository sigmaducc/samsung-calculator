package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clearBtn = findViewById<Button>(R.id.clear)
        val leftParenthesisBtn = findViewById<Button>(R.id.left_parenthesis)
        val rightParenthesisBtn = findViewById<Button>(R.id.right_parenthesis)
        val divideBtn = findViewById<Button>(R.id.divide)
        val multiplyBtn = findViewById<Button>(R.id.multiply)
        val subtractBtn = findViewById<Button>(R.id.minus)
        val addBtn = findViewById<Button>(R.id.plus)
        val equalsBtn = findViewById<Button>(R.id.equals)
        val decimalBtn = findViewById<Button>(R.id.dot)
        val backspaceBtn = findViewById<Button>(R.id.backspace)

        val zero = findViewById<Button>(R.id.zero)
        val one = findViewById<Button>(R.id.one)
        val two = findViewById<Button>(R.id.two)
        val three = findViewById<Button>(R.id.three)
        val four = findViewById<Button>(R.id.four)
        val five = findViewById<Button>(R.id.five)
        val six = findViewById<Button>(R.id.six)
        val seven = findViewById<Button>(R.id.seven)
        val eight = findViewById<Button>(R.id.eight)
        val nine = findViewById<Button>(R.id.nine)

        val expr = findViewById<TextView>(R.id.expr)

        clearBtn.setOnClickListener { expr.text = "" }
        equalsBtn.setOnClickListener { expr.text = evaluateExpr(expr) }
        leftParenthesisBtn.setOnClickListener { appendToExpr( expr, " ( " ) }
        rightParenthesisBtn.setOnClickListener { appendToExpr( expr, " ) " ) }
        divideBtn.setOnClickListener { appendToExpr( expr, " / " ) }
        multiplyBtn.setOnClickListener { appendToExpr( expr, " * " ) }
        subtractBtn.setOnClickListener { appendToExpr( expr, " - " ) }
        addBtn.setOnClickListener { appendToExpr( expr, " + " ) }

        decimalBtn.setOnClickListener { appendToExpr( expr, "." ) }

        backspaceBtn.setOnClickListener{ popFromExpr(expr) }

        one.setOnClickListener { appendToExpr( expr, "1" ) }
        two.setOnClickListener { appendToExpr( expr, "2" ) }
        three.setOnClickListener { appendToExpr( expr, "3" ) }
        four.setOnClickListener { appendToExpr( expr, "4" ) }
        five.setOnClickListener { appendToExpr( expr, "5" ) }
        six.setOnClickListener { appendToExpr( expr, "6" ) }
        seven.setOnClickListener { appendToExpr( expr, "7" ) }
        eight.setOnClickListener { appendToExpr( expr, "8" ) }
        nine.setOnClickListener { appendToExpr( expr, "9" ) }
        zero.setOnClickListener { appendToExpr( expr, "0" ) }

//        Log.i("MainActivity", evaluatePostfixExpr("100 200 + 2 / 5 * 7 +").roundToInt().toString());
//        Log.i("MainActivity", infixToPostfix("A / ( B - C + D )"));
//        Log.i("MainActivity", infixToPostfix("(A / (B - C + D)) * (E - A) * C"));
    }

    private fun evaluateExpr( expr: TextView ): String{
        return evaluatePostfixExpr( infixToPostfix( expr.text.toString() ) ).roundToInt().toString()

//        val eval = ExpressionBuilder(text).build()
//        lateinit var res: String
//        try {
//            res = eval.evaluate().roundToInt().toString()
//        } catch(e: ArithmeticException){
//            Log.e("MainActivity", "Division by Zero not allowed!")
//        }
//        return res
    }

    private fun appendToExpr( expr: TextView, char: String ){
        expr.text = expr.text.toString() + char
    }

    private fun popFromExpr( expr: TextView ){
        if( expr.text.toString().isNotEmpty() ){
            expr.text = expr.text.toString().dropLast(1)
        }
    }

    private fun infixToPostfix( expr: String ): String {
        val values = expr.trim().split(" ")
        var postfix = ""
        val operators = Stack<String>()
        for (value in values){
            if ( value.isEmpty() ) continue
            else if( isNumeric(value) ) postfix += " $value "
            else if( value == "(" ){
                operators.push(value)
            }
            else if( value == ")" ){
                while( operators.peek() != "(" ){
                    postfix += " ${operators.pop()} "
                }
                operators.pop()
            }
            else{
                while ( operators.isNotEmpty()
                    && operators.peek() != "("
                    && precedence(value) <= precedence(operators.peek()) ){
                    postfix += " ${operators.pop()} "
                }
                operators.push(value)
            }
        }
        while ( operators.isNotEmpty() && operators.peek() != "(" ){
            postfix += " ${operators.pop()} "
        }
        return postfix;
    }

    private fun evaluatePostfixExpr( expr: String ): Float {
        val stack = Stack<Float>()
        val values = expr.trim().split(" ")
        for ( value in values ) {
            if( value.isEmpty() ) continue
            else if ( isNumeric(value) ) {
                stack.push( value.toFloat() )
            }
            else if( isOperator(value) ) {
                val top: Float = stack.pop()
                val bottom: Float = stack.pop()
                val temp: Float = when ( value ) {
                    "+" -> bottom + top
                    "-" -> bottom - top
                    "*" -> bottom * top
                    "/" -> if ( top != 0f ) bottom / top else throw ArithmeticException("Division by Zero not allowed!")
                    else -> throw Exception("Other operators not implemented")
                }
                stack.push( temp )
            }
        }
        return stack.peek()
    }

    private fun isAlphabetic( value: Char ): Boolean{
        return ( value in 'A'..'Z' )
    }

    private fun isNumeric( input: String ): Boolean {
        val integerChars = '0'..'9'
        var dotOccurred = 0
        return input.all { it in integerChars || it == '.' && dotOccurred++ < 1 }
    }

    private fun isOperator( value: String ): Boolean {
        return ( value == "+" || value == "-" || value == "*" || value == "/" )
    }

    private fun precedence( operator: String ): Int{
        if ( operator == "+" || operator == "-" )
            return 1
        if ( operator == "*" || operator == "/" || operator == "%" )
            return 2
        return 0
    }
}