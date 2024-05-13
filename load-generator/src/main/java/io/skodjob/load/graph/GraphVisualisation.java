/*
 *
 * MIT License
 *
 * Copyright (c) [2024] [Ondrej Babec <ond.babec@gmail.com>, Jiri Novotny <novotnyjirkajn@gmail.com>]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE
 * ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.skodjob.load.graph;

import com.indvd00m.ascii.render.Point;
import com.indvd00m.ascii.render.Region;
import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Rectangle;
import com.indvd00m.ascii.render.elements.plot.Axis;
import com.indvd00m.ascii.render.elements.plot.AxisLabels;
import com.indvd00m.ascii.render.elements.plot.Plot;
import com.indvd00m.ascii.render.elements.plot.api.AxisType;
import com.indvd00m.ascii.render.elements.plot.api.IPlotPoint;
import com.indvd00m.ascii.render.elements.plot.misc.AxisLabel;
import com.indvd00m.ascii.render.elements.plot.misc.PlotPoint;
import io.skodjob.load.scenarios.ScenarioRequest;
import io.skodjob.load.scenarios.ScenarioRequestExecutor;

import java.util.ArrayList;
import java.util.List;

public class GraphVisualisation {
    public static String drawGraph(ScenarioRequestExecutor executor) {
        List<IPlotPoint> points = new ArrayList<IPlotPoint>();
        int pos = 0;
        int maxHeight = 0;
        List<ScenarioRequest> scs = executor.getRequestScenario();
        int roundCount = scs.size();

        for (ScenarioRequest sc : scs) {
            maxHeight = Math.max(maxHeight, sc.getBatchSize());
            points.add(new PlotPoint(pos, sc.getBatchSize()));
            pos += 5;
        }

        maxHeight = Math.min(30, maxHeight);
        roundCount = Math.max(roundCount, 200);
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(roundCount + 14).height(maxHeight + 10);
        builder.element(new Rectangle(0, 0, roundCount + 14, maxHeight + 10));
        builder.layer(new Region(1, 2, roundCount + 10, maxHeight + 10));

        builder.element(new Axis(points, new Region(12, 0, roundCount, maxHeight + 4)));
        builder.element(new AxisLabels(points, new Region(12, 0, roundCount, maxHeight + 4)));
        builder.element(new AxisLabel("Requests", 2, 2, AxisType.Y, new Point(0, 0)));
        builder.element(new AxisLabel("Rate step", roundCount - 2, maxHeight + 4, AxisType.X, new Point(0, 0)));
        builder.element(new Plot(points, new Region(12, 0, roundCount + 10, maxHeight + 4)));

        ICanvas canvas = render.render(builder.build());
        return canvas.toString();
    }
}
