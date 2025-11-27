import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Pie } from 'react-chartjs-2'

ChartJS.register(ArcElement, Tooltip, Legend)

export function IncomeCategoryChart({ incomes}) {

    const getCategoryData = () => {
    const categoryData = {}
    let totalAmount = 0
    
    incomes.forEach(income => {
        const category = income.category || 'Others'
        if (!categoryData[category]) {
        categoryData[category] = 0
        }
        categoryData[category] += income.amount
        totalAmount += income.amount
    })
    
    return {categoryData: categoryData, totalAmount: totalAmount}
    }

    const categoryData = getCategoryData()
    const categories = Object.keys(categoryData.categoryData)
    const amounts = Object.values(categoryData.categoryData)


    const backgroundColors = [
    '#ff7795',    
    '#ffb26b',    
    '#fffbf3',   
    ]

    const borderColors = backgroundColors.map(color => color.replace('0.8', '1'))

    const chartData = {
    labels: categories,
    datasets: [
        {
        data: amounts,
        backgroundColor: backgroundColors.slice(0, categories.length),
        borderColor: borderColors.slice(0, categories.length),
        borderWidth: 1 ,
        hoverOffset: 15,
        },
    ],
    }

    const options = {
    responsive: true,
    maintainAspectRatio: false,
    layout:{
        padding: {
            top: 10,    
            bottom: 15,  
            left: 10,
            right: 10
        }
    },
    plugins: {
        legend: {
        position: 'bottom' ,
        labels: {
            color: 'white',
            font: {
            size: 12,
            },
            boxWidth:  12 ,
            padding:  10 ,
        },
        },
        tooltip: {
        callbacks: {
            label: function(context) {
            const label = context.label || ''
            const value = context.parsed
            const total = context.dataset.data.reduce((a, b) => a + b, 0)
            const percentage = ((value / total) * 100).toFixed(1)
            return `${label}: €${value.toFixed(2)} (${percentage}%)`
            }
        },
        },
    },
    }

    return (
    <div className="chart-card pie-chart-container">
        <h4 className="chart-title">
        Incomes by Category
            <div className="chart-subtitle">
                Total: €{categoryData.totalAmount.toFixed(2)}
            </div>
        </h4>
        <div className="chart-content">
            {categories.length > 0 ? (
            <Pie data={chartData} options={options} />
            ) : (
            <p className='chart-no-data'>
                No incomes to show
            </p>
            )}
        </div>
    </div>
    )
}