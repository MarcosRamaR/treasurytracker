import React from 'react';
import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export function MonthlyChart({ expenses, incomes }) {

    const getMonthlyData = () => {
    const monthlyData = {};
    
    expenses.forEach(expense => {
        const date = new Date(expense.date);
        const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        const monthName = date.toLocaleString('default', { month: 'long', year: 'numeric' });

        if (!monthlyData[monthYear]) {
        monthlyData[monthYear] = {
            month: monthName,
            expenses: 0,
            incomes: 0
        };
        }
        monthlyData[monthYear].expenses += expense.amount;
    });
    
    incomes.forEach(income => {
        const date = new Date(income.date);
        const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        const monthName = date.toLocaleString('default', { month: 'long', year: 'numeric' });

        if (!monthlyData[monthYear]) {
        monthlyData[monthYear] = {
            month: monthName,
            expenses: 0,
            incomes: 0
        };
        }
        monthlyData[monthYear].incomes += income.amount;
    });
    
    return Object.values(monthlyData).sort((a, b) => {
        return new Date(a.month) - new Date(b.month);
    });
    };

    const monthlyData = getMonthlyData();

    const chartData = {
    labels: monthlyData.map(data => data.month),
    datasets: [
        {
        label: 'Expenses',
        data: monthlyData.map(data => data.expenses),
        backgroundColor: 'rgba(229, 115, 115, 0.8)',
        borderColor: 'rgba(229, 115, 115, 1)',
        borderWidth: 2,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(229, 115, 115, 1)',
        },
        {
        label: 'Incomes',
        data: monthlyData.map(data => data.incomes),
        backgroundColor: 'rgba(129, 199, 132, 0.8)',
        borderColor: 'rgba(129, 199, 132, 1)',
        borderWidth: 2,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(129, 199, 132, 1)',
        },
    ],
    };

    const options = {
    responsive: true,
    plugins: {
        legend: {
        position: 'top',
        labels:{
            color: 'white',
        }
        },
        title: {
        display: true,
        text: 'Monthly Comparison',
        color: 'white',
        },
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            color: '#e0e0e0', 
        },
        title: {
            display: true,
            text: 'Amount (€)',
            color: 'white',
        }
        },
        x: {
        ticks: {
            color: '#e0e0e0', 
        },
        title: {
            display: true,
            text: 'Months',
            color: 'white',
        }
        }
    },
    };

    return (
    <div style={{ margin: '20px 0', padding: '20px', backgroundColor: '#16213e', borderRadius: '8px' }}>
        {monthlyData.length > 0 ? (
        <Bar data={chartData} options={options} />
        ) : (
        <p>No data to show</p>
        )}
    </div>
    );
}