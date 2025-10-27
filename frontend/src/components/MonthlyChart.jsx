import React from 'react';
import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export function MonthlyChart({ expenses, incomes,size = 'small'  }) {

    const getMonthlyData = () => {
    const monthlyData = {};
    
    expenses.forEach(expense => {
        const date = new Date(expense.date);
        const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        const monthName = size === 'small' 
            ? date.toLocaleString('default', { month: 'short', year: 'numeric' })
            : date.toLocaleString('default', { month: 'long', year: 'numeric' }); //LocaleString for month name

        if (!monthlyData[monthYear]) {
        monthlyData[monthYear] = {
            month: monthName,
            monthKey: monthYear,
            expenses: 0,
            incomes: 0
        };
        }
        monthlyData[monthYear].expenses += expense.amount;
    });
    
    incomes.forEach(income => {
        const date = new Date(income.date);
        const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        const monthName = size === 'small' 
            ? date.toLocaleString('default', { month: 'short', year: 'numeric' }) 
            : date.toLocaleString('default', { month: 'long', year: 'numeric' });

        if (!monthlyData[monthYear]) {
        monthlyData[monthYear] = {
            month: monthName,
            monthKey: monthYear,
            expenses: 0,
            incomes: 0
        };
        }
        monthlyData[monthYear].incomes += income.amount;
    });
    
    return Object.values(monthlyData).sort((a, b) => {
        //Compare by monthKey to ensure chronological order
        return a.monthKey.localeCompare(b.monthKey);
    });
    };

    const monthlyData = getMonthlyData();

    const chartData = {
    labels: monthlyData.map(data => data.month),
    datasets: [
        {
        label: 'Expenses',
        data: monthlyData.map(data => data.expenses), //Expenses data each month in order
        backgroundColor: 'rgba(229, 115, 115, 0.8)',
        borderColor: 'rgba(229, 115, 115, 1)',
        borderWidth: size === 'small' ? 1 : 2,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(229, 115, 115, 1)',
        },
        {
        label: 'Incomes',
        data: monthlyData.map(data => data.incomes),
        backgroundColor: 'rgba(129, 199, 132, 0.8)',
        borderColor: 'rgba(129, 199, 132, 1)',
        borderWidth: size === 'small' ? 1 : 2,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(129, 199, 132, 1)',
        },
    ],
    };

    const options = {
    responsive: true,
    maintainAspectRatio: false, //Allow CSS to control
    plugins: {
        legend: {
        position: size === 'small' ? 'bottom' : 'top',
        labels: {
            color: 'white',
            font: {
            size: size === 'small' ? 12 : 14,
            },
            boxWidth: size === 'small' ? 12 : 15,
        }
        },
        tooltip: {
            bodyFont: {
            size: size === 'small' ? 11 : 12,
            },
            titleFont: {
            size: size === 'small' ? 12 : 13,
            }
        }
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            color: '#e0e0e0',
            font: {
            size: size === 'small' ? 10 : 12,
            },
            maxTicksLimit: size === 'small' ? 5 : 8,
        },
        title: {
            display: size !== 'small',
            text: 'Amount (€)',
            color: 'white',
        },
        grid: {
            color: size === 'small' ? 'rgba(255,255,255,0.1)' : 'rgba(255,255,255,0.2)',
        }
        },
        x: {
        ticks: {
            color: '#e0e0e0',
            font: {
            size: size === 'small' ? 10 : 12,
            },
            maxRotation: size === 'small' ? 45 : 0,
        },
        title: {
            display: size !== 'small',
            text: 'Months',
            color: 'white',
        },
        grid: {
            display: size !== 'small',
        }
        }
    }
    };
    const containerStyle = {
    margin: size === 'small' ? '10px 0' : '20px 0',
    padding: size === 'small' ? '12px' : '20px',
    backgroundColor: '#16213e', 
    borderRadius: '8px',
    height: size === 'small' ? '250px' : 'auto',
    width: size === 'small' ? '500px' : '100%',
    minHeight: size === 'small' ? '250px' : '400px',
    };

    return (
    <div style={containerStyle}>
        {monthlyData.length > 0 ? (
        <Bar data={chartData} options={options} />
        ) : (
        <p>No data to show</p>
        )}
    </div>
    );
}