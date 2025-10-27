import React from 'react';
import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export function MonthlyChart({ expenses, incomes}) {

    const getMonthlyData = () => {
    const monthlyData = {};
    
    expenses.forEach(expense => {
        const date = new Date(expense.date);
        const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}`;
        const monthName = date.toLocaleString('default', { month: 'short', year: 'numeric' }); //LocaleString for month name

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
        const monthName = date.toLocaleString('default', { month: 'short', year: 'numeric' });

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
        borderWidth: 1 ,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(229, 115, 115, 1)',
        },
        {
        label: 'Incomes',
        data: monthlyData.map(data => data.incomes),
        backgroundColor: 'rgba(129, 199, 132, 0.8)',
        borderColor: 'rgba(129, 199, 132, 1)',
        borderWidth:  1 ,
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
        position: 'bottom' ,
        labels: {
            color: 'white',
            font: {
            size: 13,
            },
            boxWidth: 20,
        }
        },
        tooltip: {
            bodyFont: {
            size:  12,
            },
            titleFont: {
            size:  13,
            }
        }
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            color: '#e0e0e0',
            font: {
            size: 11,
            },
            maxTicksLimit: 5,
        },
        title: {
            text: 'Amount (€)',
            color: 'white',
        },
        grid: {
            color: 'rgba(255,255,255,0.1)' ,
        }
        },
        x: {
        ticks: {
            color: '#e0e0e0',
            font: {
            size:  11,
            },
            maxRotation: 45 ,
        },
        title: {
            text: 'Months',
            color: 'hite',
        }
        }
    }
    };

    return (
    <div className="chart-card bar-chart-container">
        <h4 className="chart-title"> Total Expenses </h4>
        <div className="chart-content">
            {monthlyData.length > 0 ? (
            <Bar data={chartData} options={options} />
            ) : (
            <p className='chart-no-data'>No data to show</p>
            )}
        </div>
    </div>
    );
}